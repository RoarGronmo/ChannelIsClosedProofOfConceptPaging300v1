package no.rogo.channelisclosedproofofconceptpaging300v1.repository.mediators

import android.provider.MediaStore
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import no.rogo.channelisclosedproofofconceptpaging300v1.api.factories.APIFamappClientFactory
import no.rogo.channelisclosedproofofconceptpaging300v1.api.responses.APIGetStationsResponse
import no.rogo.channelisclosedproofofconceptpaging300v1.room.db.AppDatabase
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.RemoteKeyEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.StationResponse
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

/**
 * Created by Roar on 16.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@OptIn(ExperimentalPagingApi::class)
class StationRemoteMediator(
        private val appDatabase: AppDatabase
): RemoteMediator<Int, StationResponse>(){

    private val TAG = javaClass.simpleName

    override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, StationResponse>
    ): MediatorResult
    {
        val page = when (loadType)
        {
            LoadType.REFRESH ->{

            }
            LoadType.PREPEND ->{

            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                if(remoteKey == null || remoteKey.nextKey == null) {
                    Log.e(TAG, "load: Remote key or next key should not be null" )
                    throw InvalidObjectException("Remote key or next key should not be null")
                }
                remoteKey.nextKey
            }
        }

        val service = APIFamappClientFactory.makeAPIFamappInterfaceService()

        try {
            val limit = "20"
            val offset = "0"

            val req = service.getStations(
                userid = "10000",
                    passfrase = "qazwsxedcrfvtgbyhnujm",
                    latitude = "61.89",
                    longitude = "6.67",
                    lastversion = "cicpoc3 rem v1.0",
                    killed = "0",
                    range = "1.0",
                    limit = limit,
                    offset = offset
            )


        }catch (exception: IOException)
        {
            Log.e(TAG, "load: IOException",exception )
            return MediatorResult.Error(exception)
        }
        catch (exception: HttpException)
        {
            Log.e(TAG, "load: HttpException", exception)
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StationResponse>):RemoteKeyEntity?
    {
        val value = state.pages.lastOrNull(){it.data.isNotEmpty()}?.data?.lastOrNull()
                ?.let { stationResponse->
                    val remoteKeyEntity = appDatabase.remoteKeyDao().getRemoteKeysFromStationPrimaryKey(stationResponse.stationPrimaryKey)
                    Log.i(TAG, "getRemoteKeyForLastItem: lookup remoteKeyEntity = $remoteKeyEntity")
                    return@let remoteKeyEntity
                }?:let { Log.w(TAG, "getRemoteKeyForLastItem: null found")
                    return@let null
                }
        Log.i(TAG, "getRemoteKeyForLastItem: found remoteKeyEntity = $value")
        return value
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StationResponse>):RemoteKeyEntity?
    {
        val value = state.pages.firstOrNull(){it.data.isNotEmpty()}?.data?.firstOrNull()
                ?.let {stationResponse ->
                    val remoteKeyEntity = appDatabase.remoteKeyDao().getRemoteKeysFromStationPrimaryKey(stationResponse.stationPrimaryKey)
                    Log.i(TAG, "getRemoteKeyForFirstItem: lookup remoteKeyEntity = $remoteKeyEntity")
                    return@let remoteKeyEntity
                }?:let { Log.w(TAG, "getRemoteKeyForFirstItem: null found")
                    return@let null
                }
        Log.i(TAG, "getRemoteKeyForFirstItem: found remoteKeyEntity = $value")
        return value
    }



}