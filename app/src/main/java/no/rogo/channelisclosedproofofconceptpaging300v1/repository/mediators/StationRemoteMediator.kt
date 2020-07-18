package no.rogo.channelisclosedproofofconceptpaging300v1.repository.mediators

import android.provider.MediaStore
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
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
                val remoteKey = getRemoteKeyForClosestItemToPosition(state)
                remoteKey?.nextKey?.minus(1)?:1
            }
            LoadType.PREPEND -> {
                Log.i(TAG, "load: PREPEND state = $state")
                val remoteKey = getRemoteKeyForFirstItem(state)
                if (remoteKey == null) {
                    Log.e(TAG, "load: PREPEND Remote key or next key should not be null")
                    throw InvalidObjectException("Remote key or next key should not be null")
                }
                //val prevKey = remoteKey.prevKey
                if (remoteKey.prevKey == null) {
                    Log.i(TAG, "load: PREPEND remoteKey.prevKey is null, no more data available ?")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                Log.i(TAG, "load: PREPEND sets page = remoteKey.prevKey = ${remoteKey.prevKey}")
                remoteKey.prevKey
            }
            LoadType.APPEND -> {
                Log.i(TAG, "load: APPEND state = $state")
                val remoteKey = getRemoteKeyForLastItem(state)
                if (remoteKey == null || remoteKey.nextKey == null) {
                    Log.e(TAG, "load: APPEND Remote key or next key should not be null")
                    throw InvalidObjectException("Remote key or next key should not be null")
                }
                Log.i(TAG, "load: APPEND sets page = remoteKey.nextKey = ${remoteKey.nextKey}")
                remoteKey.nextKey
            }
        }

        Log.i(TAG, "load: page = $page")

        val service = APIFamappClientFactory.makeAPIFamappInterfaceService()

        try {
            val ilimit= 20

            val limit = ilimit.toString()
            val offset = (page*(ilimit-1)).toString()

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

            Log.i(TAG, "load: req.size = ${req.size }")

            appDatabase.withTransaction {
                if(loadType == LoadType.REFRESH)
                {
                    Log.i(TAG, "load: REFRESH clearing remotekeys and stations")
                    appDatabase.remoteKeyDao().clearRemoteKeys()
                    appDatabase.stationDao().clearStations()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page+1
                val keys = req.map {
                    RemoteKeyEntity(
                            stationPrimaryKey = ,

                    )
                }
            }



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

    private suspend fun getRemoteKeyForClosestItemToPosition(
            state: PagingState<Int, StationResponse>
    ):RemoteKeyEntity?
    {
        val value1 = state.anchorPosition?.let { safeAnchorPosition->
            Log.i(TAG, "getRemoteKeyForClosestItemToPosition: safeAnchorPosition = $safeAnchorPosition")
            val value2 = state.closestItemToPosition(safeAnchorPosition)?.stationPrimaryKey
                    ?.let {safeStationPrimaryKey ->
                        Log.i(TAG, "getRemoteKeyForClosestItemToPosition: safeStationPrimaryKey = $safeStationPrimaryKey")
                        val remoteKeyEntity = appDatabase.remoteKeyDao().getRemoteKeysFromStationPrimaryKey(safeStationPrimaryKey)
                        Log.i(TAG, "getRemoteKeyForClosestItemToPosition: " +
                                "getRemoteKeysFromStationPrimaryKey lookup remoteKeyEntity " +
                                "= $remoteKeyEntity")
                        return@let remoteKeyEntity
                    }?:let {
                        Log.w(TAG, "getRemoteKeyForClosestItemToPosition: " +
                                "getRemoteKeysFromStationPrimaryKey lookup found null")
                        return@let null
                    }
            Log.i(TAG, "getRemoteKeyForClosestItemToPosition: closestItemToPosition(safeAnchorPosition) " +
                    "(value2) found = $value2")
            return@let value2
        }?:let {
            Log.w(TAG, "getRemoteKeyForClosestItemToPosition: closesItemToPosition(safeAnchorPosition) is null"  )
            return@let null
        }

        Log.i(TAG, "getRemoteKeyForClosestItemToPosition: remoteKeyForClosestItemToPosition (value1) = $value1 ")

        return value1
    }



}