package no.rogo.channelisclosedproofofconceptpaging300v1.repository.repositories

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import androidx.room.withTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.rogo.channelisclosedproofofconceptpaging300v1.api.factories.APIFamappClientFactory
import no.rogo.channelisclosedproofofconceptpaging300v1.api.responses.APIGetStationsResponse
import no.rogo.channelisclosedproofofconceptpaging300v1.repository.mediators.StationRemoteMediator
import no.rogo.channelisclosedproofofconceptpaging300v1.repository.pagingsources.StationPagingSource
import no.rogo.channelisclosedproofofconceptpaging300v1.room.db.AppDatabase
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.DeviceLocationEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.StationResponse

/**
 * Created by Roar on 14.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
class CommonDatabaseRepository private constructor(
        private val appDatabase: AppDatabase
){
    private val TAG = javaClass.simpleName

    lateinit var stationsPagingSource: PagingSource<Int, StationResponse>

    fun insertLocation(location: Location)
    {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.withTransaction {
                appDatabase.deviceLocationDao().insertLocation(
                        DeviceLocationEntity(
                                deviceLocationPrimaryKey = 0,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                time = location.time
                        )
                )
                Log.i(TAG, "insertLocation: attempted to insert location=$location")
            }
        }
    }

    fun getLiveDataPagingDataStationResponses():LiveData<PagingData<StationResponse>>
    {
        Log.i(TAG, "getLiveDataPagingDataStationResponses: ()")

        val stationsPagingSourceFactory = {

            stationsPagingSource = appDatabase.stationDao().getPagedStationResponses()

            stationsPagingSource
        }

        return Pager(
                config = PagingConfig(pageSize = 20),
                remoteMediator = StationRemoteMediator(appDatabase),
                pagingSourceFactory = stationsPagingSourceFactory
        ).liveData
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

        fun getLiveDataPagingDataStationResponse():LiveData<PagingData<StationResponse>>?
        {
            Log.i(TAG, "getLiveDataPagingDataStationResponse: instance = $instance")
            return instance?.getLiveDataPagingDataStationResponses()
        }



    }


}