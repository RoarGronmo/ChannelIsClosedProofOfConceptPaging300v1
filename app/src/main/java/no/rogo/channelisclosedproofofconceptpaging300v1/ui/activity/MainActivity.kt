package no.rogo.channelisclosedproofofconceptpaging300v1.ui.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.provider.Settings
import android.util.Log
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import no.rogo.channelisclosedproofofconceptpaging300v1.BuildConfig
import no.rogo.channelisclosedproofofconceptpaging300v1.R
import no.rogo.channelisclosedproofofconceptpaging300v1.services.ForegroundOnlyLocationService
import no.rogo.channelisclosedproofofconceptpaging300v1.ui.main.MainFragment
import no.rogo.channelisclosedproofofconceptpaging300v1.ui.main.MainViewModel
import no.rogo.channelisclosedproofofconceptpaging300v1.utils.SharedPreferenceUtil
import no.rogo.channelisclosedproofofconceptpaging300v1.utils.toText

private const val REQUEST_FOREGROUND_ONLY_PERMISSION_REQUEST_CODE = 34

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG by lazy { this::class.java.simpleName }

    private var foregroundOnlyLocationServiceBound = false

    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    private lateinit var foregroundOnlyBroadCastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var mainViewModel:MainViewModel

    private val foregroundOnlyServiceConnetion = object : ServiceConnection{
        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            Log.d(TAG, "onServiceConnected: (componentName = $componentName, iBinder = $iBinder)")

            val binder = iBinder as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
            Log.d(TAG, "onServiceConnected: foregroundOnlyLocationService = $foregroundOnlyLocationService")
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: (componentName = $componentName)")
            Log.d(TAG, "onServiceDisconnected: foregroundOnlyLocationService" +
                    " = $foregroundOnlyLocationService")
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }

    }



    private inner class ForegroundOnlyBroadcastReceiver: BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: (context = $context, intent = $intent)")
            val location = intent?.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )
            Log.d(TAG, "onReceive: Foreground location = $location")
            if(location != null) {
                //logResultsToScreen("Foreground location: ${location.toText()}")
                mainViewModel.lastLocationMLD.postValue(location)
                Log.i(TAG, "onReceive: Should send location to database")
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate: (savedInstanceState = $savedInstanceState)")

        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

        foregroundOnlyBroadCastReceiver = ForegroundOnlyBroadcastReceiver()

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        mainViewModel.switchEnableGPSUpdateStateMLD.observe(this) { switchEnableGPSUpdateState->
            Log.d(TAG, "onCreate: observing mainViewModel.switchEnableGPSUpdateStateMLD = $switchEnableGPSUpdateState")
            if(switchEnableGPSUpdateState == true)
            {
                if(foregroundPermissionApproved())
                {
                    Log.d(TAG, "onCreate: switchEnableGPSUpdateStateMLD.observes switch" +
                            " == true, subscribing locationservice foregroundOnlyLocationService " +
                            "= $foregroundOnlyLocationService")
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()

                }else
                {
                    requestForegroundPermissions()
                }
            }else
            {
                Log.d(
                    TAG,
                    "onCreate: switchEnableGPSUpdateStateMLD.observes switch == false or " +
                            "null, unsubscribing locationservice foregroundOnlyLocationService" +
                            " = $foregroundOnlyLocationService"
                )
                foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
                    ?: Log.d(TAG, "onCreate: switchEnableGPSUpdateStateMLD was not bound")
            }
        }

    }

    override fun onStart() {
        Log.d(TAG, "onStart: ()")

        super.onStart()

        updateGPSwitchState(
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnetion, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        Log.d(TAG, "onResume: ()")
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadCastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        Log.d(TAG, "onPause: ()")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadCastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop: ()")
        if(foregroundOnlyLocationServiceBound)
        {
            Log.d(TAG, "onStop: unbinding foreground service")
            unbindService(foregroundOnlyServiceConnetion)
            foregroundOnlyLocationServiceBound = false
        }
        Log.d(TAG, "onStop: unregistering sharedPreferenceChangeListener")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "onSharedPreferenceChanged: ()")
        if(key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED){
            sharedPreferences?.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
            )?.let { updateGPSwitchState(it) }
                ?: updateGPSwitchState(false)
        }
    }



    private fun foregroundPermissionApproved() :Boolean
    {
        Log.d(TAG, "foregroundPermissionApproved: ()")
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestForegroundPermissions()
    {
        Log.d(TAG, "requestForegroundPermissions: ()")
        val provideRationale = foregroundPermissionApproved()

        if(provideRationale)
        {
            Log.d(TAG, "requestForegroundPermissions: request permission message second attempt")
            Snackbar.make(
                findViewById(R.id.container),
                "Location needed for core functionality",
                Snackbar.LENGTH_LONG
            )
                .setAction("OK"){
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSION_REQUEST_CODE
                    )
                }
                .show()
        }
        else
        {
            Log.d(TAG, "requestForegroundPermissions: Request foreground only permission")
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: (requestCode = $requestCode, " +
                    "permissions=$permissions, grantResults=$grantResults)")

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode)
        {
            REQUEST_FOREGROUND_ONLY_PERMISSION_REQUEST_CODE -> when {
                grantResults.isEmpty() -> {
                    Log.d(TAG, "onRequestPermissionsResult: User interaction was cancelled")
                }

                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "onRequestPermissionsResult: User granted permission")
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                }

                else -> {
                    Log.d(TAG, "onRequestPermissionsResult: User did not grant permission")

                    updateGPSwitchState(false)

                    Snackbar.make(
                        findViewById(R.id.container),
                        "Permission was denied, but is needed for core functions...",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Settings"){
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null)
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }

            }
        }
    }

    private fun updateGPSwitchState(trackingLocation: Boolean)
    {
        Log.d(TAG, "updateGPSwitchState: (trackingLocation = $trackingLocation)")
        if(trackingLocation)
        {
            Log.d(TAG, "updateGPSwitchState: attempt to turn on")
            if(mainViewModel.switchEnableGPSUpdateStateMLD.value==false) {
                Log.d(TAG, "updateGPSwitchState: turning on")
                mainViewModel.switchEnableGPSUpdateStateMLD.value = true
            }
        }
        else
        {
            Log.d(TAG, "updateGPSwitchState: attempt to turn off")
            if(mainViewModel.switchEnableGPSUpdateStateMLD.value==true) {
                Log.d(TAG, "updateGPSwitchState: turning off")
                mainViewModel.switchEnableGPSUpdateStateMLD.value = false
            }
        }
    }



}