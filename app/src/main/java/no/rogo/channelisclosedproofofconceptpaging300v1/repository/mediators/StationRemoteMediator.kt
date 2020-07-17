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
import retrofit2.HttpException
import java.io.IOException

/**
 * Created by Roar on 16.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@OptIn(ExperimentalPagingApi::class)
class StationRemoteMediator(
        private val appDatabase: AppDatabase
): RemoteMediator<Int, APIGetStationsResponse>(){

    private val TAG = javaClass.simpleName

    override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, APIGetStationsResponse>
    ): MediatorResult
    {
        val page = when (loadType)
        {
            LoadType.REFRESH ->{

            }
            LoadType.PREPEND ->{

            }
            LoadType.APPEND ->{

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
                    lastversion = "test v1.0",
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

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int,APIGetStationsResponse>):RemoteKeyEntity
    {
        val value = state.pages.lastOrNull(){it.data.isNotEmpty()}?.data?.lastOrNull()
                ?.let { apiGetStationsResponse->
                    val key = appDatabase.remoteKeyDao().getRemoteKeysFromStationId(apiGetStationsResponse.idsite)
                }

    }

}