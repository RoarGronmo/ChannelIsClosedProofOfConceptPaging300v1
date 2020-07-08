package no.rogo.channelisclosedproofofconceptpaging300v1.ui.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.util.Log
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import no.rogo.channelisclosedproofofconceptpaging300v1.R
import no.rogo.channelisclosedproofofconceptpaging300v1.services.ForegroundOnlyLocationService
import no.rogo.channelisclosedproofofconceptpaging300v1.ui.main.MainFragment
import no.rogo.channelisclosedproofofconceptpaging300v1.ui.main.MainViewModel
import no.rogo.channelisclosedproofofconceptpaging300v1.utils.toText

private const val REQUEST_FOREGROUND_ONLY_PERMISSION_REQUEST_CODE = 34

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG by lazy { this::class.java.simpleName }

    private var foregroundOnlyLocationServiceBound = false

    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    private lateinit var foregroundOnlyBroadCastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var foregroundOnlySwitch: Switch

    private lateinit var mainViewModel:MainViewModel

    private val foregroundOnlyServiceConnetion = object : ServiceConnection{
        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
            Log.d(TAG, "onServiceConnected: componentName = $componentName, iBinder.interfaceDescriptor = ${iBinder.interfaceDescriptor}")
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
            Log.d(TAG, "onServiceDisconnected: componentName = $componentName")
        }

    }



    private inner class ForegroundOnlyBroadcastReceiver: BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            val location = intent?.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )
            if(location != null) {
                //logResultsToScreen("Foreground location: ${location.toText()}")
                Log.i(TAG, "onReceive: Foreground location: ${location.toText()}")
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
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

    private fun foregroundPermissionApproved() :Boolean
    {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestForegroundPermissions()
    {
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

                    Snackbar.make(
                        findViewById(R.id.container)
                    )
                }

            }
        }
    }

    private fun updateGPSwitchState(trackingLocation: Boolean)
    {
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
            if(mainViewModel.switchEnableGPSUpdateStateMLD.value==true)
            {
                Log.d(TAG, "updateGPSwitchState: turning off")
                mainViewModel.switchEnableGPSUpdateStateMLD.value = false
            }
        }
    }


    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("Not yet implemented")
    }
}