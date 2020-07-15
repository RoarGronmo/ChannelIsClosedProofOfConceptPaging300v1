package no.rogo.channelisclosedproofofconceptpaging300v1.repository.repositories

import android.location.Location
import android.util.Log
import androidx.room.withTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.rogo.channelisclosedproofofconceptpaging300v1.room.db.AppDatabase
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.DeviceLocationEntity

/**
 * Created by Roar on 14.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
class CommonDatabaseRepository private constructor(
        private val appDatabase: AppDatabase
){
    private val TAG = javaClass.simpleName

    fun insertLocation(location: Location)
    {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.withTransaction {
                appDatabase.deviceLocationDao().insertLocation(
                        DeviceLocationEntity(
                                key = 0,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                time = location.time
                        )
                )
                Log.i(TAG, "insertLocation: attempted to insert location=$location")
            }
        }
    }


    companion object
    {
        private val TAG = this::class.java.simpleName

        @Volatile private var instance: CommonDatabaseRepository?=null

        fun getInstance(
                appDatabase: AppDatabase
        ) = instance ?: synchronized(this)
        {
            instance ?: CommonDatabaseRepository(appDatabase).also { instance = it }
        }

        //-------------------------------------------------------------------------------------

        fun insertLocation(location: Location)
        {
            Log.i(TAG, "insertLocation: instance = $instance, location = $location")
            instance?.insertLocation(location)
        }

    }


}