package no.rogo.channelisclosedproofofconceptpaging300v1.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.android.gms.location.*
import no.rogo.channelisclosedproofofconceptpaging300v1.R
import no.rogo.channelisclosedproofofconceptpaging300v1.ui.activity.MainActivity
import no.rogo.channelisclosedproofofconceptpaging300v1.utils.SharedPreferenceUtil
import no.rogo.channelisclosedproofofconceptpaging300v1.utils.toText
import java.util.concurrent.TimeUnit

/**
 * Created by Roar on 01.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */

class ForegroundOnlyLocationService  : Service()
{

    private val TAG by lazy { this::class.java.simpleName }

    private var configurationChange = false
    private var serviceRunningInForeground = false
    private val localBinder = LocalBinder()

    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null

    companion object{
        private const val PACKAGE_NAME = "no.rogo.channelisclosedproofofconceptpaging300v1"

        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 12345678

        private const val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01"
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: () ")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(2)
            fastestInterval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(3)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                if(locationResult?.lastLocation != null)
                {
                    currentLocation = locationResult.lastLocation
                    val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, currentLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                    if(serviceRunningInForeground)
                    {
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            generateNotification(currentLocation))
                    }
                }
                else
                {
                    Log.d(TAG, "onLocationResult: Location missing in callback")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand: ()")

        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if(cancelLocationTrackingFromNotification)
        {
            unsubscribeToLocationUpdates()
            stopSelf()
        }

        return START_NOT_STICKY

    }



    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG, "onBind: ()")

        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind: ()")

        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: ()")
        if(!configurationChange && SharedPreferenceUtil.getLocationTrackingPref(this))
        {
            Log.d(TAG, "onUnbind: Start foreground service")
            val notification = generateNotification(currentLocation)
            startForeground(NOTIFICATION_ID,notification)
            serviceRunningInForeground = true
        }

        return true
    }

    inner class LocalBinder : Binder() {
        internal val service: ForegroundOnlyLocationService
            get() = this@ForegroundOnlyLocationService
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }



    fun subscribeToLocationUpdates()
    {
        Log.d(TAG, "subscribeToLocationUpdates: ()")
        SharedPreferenceUtil.saveLocationTrackingPref(this, true)
        startService(Intent(applicationContext, ForegroundOnlyLocationService::class.java))
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
            )
        }catch (unlikely: SecurityException)
        {
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
            Log.e(TAG, "subscribeToLocationUpdates: Lost location permission. Couldn't request updates")
        }
    }

    fun unsubscribeToLocationUpdates()
    {
        Log.d(TAG, "unsubscribeToLocationUpdates: ()")
        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d(TAG, "unsubscribeToLocationUpdates: Location Callback removed")
                    stopSelf()
                }
                else
                {
                    Log.e(TAG, "unsubscribeToLocationUpdates: Failed to remove Location Callback")
                }
            }
            SharedPreferenceUtil.saveLocationTrackingPref(this,false)
        }
        catch (unlikely: SecurityException){
            SharedPreferenceUtil.saveLocationTrackingPref(this,true)
            Log.e(TAG, "unsubscribeToLocationUpdates: Lost location, couldn't remove updates. $unlikely")
        }
    }

    private fun generateNotification(location:Location?): Notification{
        Log.d(TAG, "generateNotification: ()")

        val mainNotificationText = location?.toText() ?: "No current location"
        val titleText = "Location in Android"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bigTextStyle = NotificationCompat
            .BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, MainActivity::class.java)
        val cancelIntent = Intent(this,ForegroundOnlyLocationService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
            this,0,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, 0
        )

        val notificationCompatBuilder = NotificationCompat.Builder(applicationContext,
            NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_baseline_launch_24, "Launch activity",
                activityPendingIntent
            )
            .addAction(
                R.drawable.ic_baseline_cancel_24, "Stop receiving location updates",
                servicePendingIntent
            )
            .build()
    }





}