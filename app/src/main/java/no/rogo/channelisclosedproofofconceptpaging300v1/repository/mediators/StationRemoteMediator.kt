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
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.PageNoResponse
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.StationResponse
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import kotlin.math.log

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
        Log.i(TAG, "load:() loadType = $loadType")
        val page = when (loadType)
        {
            LoadType.REFRESH ->{
                val pageNoResponse = getPageNoResponseForClosestItemToPosition(state)
                pageNoResponse?.pageNo?:1
            }
            LoadType.PREPEND -> {
                Log.i(TAG, "load: PREPEND state = $state")
                val pageNoResponse = getPageNoResponseForFirstItem(state)
                if ((pageNoResponse == null)||(pageNoResponse.pageNo == null)) {
                    Log.e(TAG, "load: PREPEND PageNoResponse or its pageNo should not be null")
                    throw InvalidObjectException("load: PREPEND PageNoResponse or its pageNo should not be null")
                }

                if (pageNoResponse.pageNo?.minus(1)?:0<1) {
                    Log.i(TAG, "load: PREPEND pageNo is 1 and start is then reached")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                Log.i(TAG, "load: PREPEND sets page pageNoResponse.pageNo?.minus(1) = ${pageNoResponse.pageNo?.minus(1)?:0}")
                pageNoResponse.pageNo?.minus(1)?:0
            }
            LoadType.APPEND -> {
                Log.i(TAG, "load: APPEND state = $state")
                val pageNoResponse = getPageNoResponseForLastItem(state)
                if (pageNoResponse == null || pageNoResponse.pageNo == null) {
                    Log.e(TAG, "load: APPEND PageNoResponse or its pageNo should not be null")
                    throw InvalidObjectException("load: APPEND PageNo should not be null")
                }
                Log.i(TAG, "load: APPEND current page PageNoResponse = $pageNoResponse")
                pageNoResponse.pageNo?.plus(1)?:1 //requesting next page or first page
            }
        }

        Log.i(TAG, "load: page = $page")

        val service = APIFamappClientFactory.makeAPIFamappInterfaceService()

        try {
            val iLimit= 20

            val limit = iLimit.toString()
            val offset = (iLimit*(page-1)).toString()

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

            val endOfPaginationReached = req.isNullOrEmpty()

            Log.i(TAG, "load: req.size = ${req.size }")

            appDatabase.withTransaction {
                if(loadType == LoadType.REFRESH)
                {
                    Log.i(TAG, "load: REFRESH clearing remotekeys and stations")
                    appDatabase.remoteKeyDao().clearRemoteKeys()
                    appDatabase.stationDao().clearStations()
                }
                val newStations = req.map { stationResponse->
                    StationEntity(
                            stationPrimaryKey = 0,
                            stationId = stationResponse.idSite,
                            stationName = stationResponse.stationName,
                            latitude = stationResponse.latitude?.toFloatOrNull(),
                            longitude = stationResponse.longitude?.toFloatOrNull(),
                            enterpriseId = stationResponse.enterpriseId?.toIntOrNull(),
                            killed = stationResponse.siteKilled=="1",
                            pageNo = page
                    )
                }

                if(loadType == LoadType.REFRESH)
                {
                    appDatabase.stationDao().clearStations()
                }
                Log.i(TAG, "load: inserting new stations : $newStations")
                appDatabase.stationDao().insertStation(newStations)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

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

    private suspend fun getPageNoResponseForLastItem(state: PagingState<Int, StationResponse>): PageNoResponse?
    {
        val value = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { stationResponse ->
                    Log.i(TAG, "getPageNoResponseForLastItem: stationResponse = $stationResponse")
                    val pageNoResponse = appDatabase.stationDao().getPageNoFromStationPrimaryKey(stationResponse.stationPrimaryKey)
                    Log.i(TAG, "getPageNoResponseForLastItem: pageNoResponse = $pageNoResponse")
                    return@let pageNoResponse
                }?:let {
            Log.w(TAG, "getPageNoResponseForLastItem: stationResponse == null")
            return@let null
        }
        Log.i(TAG, "getPageNoResponseForLastItem: return value = $value")

        return value
    }

    private suspend fun getPageNoResponseForFirstItem(state: PagingState<Int, StationResponse>): PageNoResponse?
    {
        Log.i(TAG, "getPageNoResponseForFirstItem: state.pages.firstOrNull() = ${state.pages.firstOrNull()}")
        val value = state.pages.firstOrNull{it.data.isNullOrEmpty()}?.data?.firstOrNull()
                ?.let {stationResponse ->
                    Log.i(TAG, "getPageNoResponseForFirstItem: stationResponse = $stationResponse")
                    val pageNoResponse = appDatabase.stationDao().getPageNoFromStationPrimaryKey(stationResponse.stationPrimaryKey)
                    Log.i(TAG, "getPageNoResponseForFirstItem: pageNoResponse = $pageNoResponse")
                    return@let pageNoResponse
                }?:let {
            Log.w(TAG, "getPageNoResponseForFirstItem: stationResponse == null")
            return@let null
        }
        Log.i(TAG, "getPageNoResponseForFirstItem: return value = $value")

        return value
    }

    private suspend fun getPageNoResponseForClosestItemToPosition(
            state: PagingState<Int, StationResponse>
    ):PageNoResponse?
    {
        val outerValue = state.anchorPosition?.let { safeAnchorPosition ->
            Log.i(TAG, "getPageNoResponseForClosestItemToPosition: safeAnchorPosition = $safeAnchorPosition")
            val innerValue = state.closestItemToPosition(safeAnchorPosition)?.stationPrimaryKey
                    ?.let { safeStationPrimaryKey->
                        Log.i(TAG, "getPageNoResponseForClosestItemToPosition: safeStationPrimaryKey = $safeStationPrimaryKey")
                        val pageNoResponse = appDatabase.stationDao().getPageNoFromStationPrimaryKey(safeStationPrimaryKey)
                        Log.i(TAG, "getPageNoResponseForClosestItemToPosition: getPageNoFromStationPrimaryKey pageNoResponse = $pageNoResponse")
                        return@let pageNoResponse
                    }?:let {
                Log.w(TAG, "getPageNoResponseForClosestItemToPosition: getPageNoResponseForClosesItemToPosition found null")
                return@let null
            }
            Log.i(TAG, "getPageNoResponseForClosestItemToPosition: return innerValue = $innerValue")
            return@let innerValue
        }

        Log.i(TAG, "getPageNoResponseForClosestItemToPosition: return outerValue = $outerValue")

        return outerValue
    }
/*

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
*/
/*
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
*/


}