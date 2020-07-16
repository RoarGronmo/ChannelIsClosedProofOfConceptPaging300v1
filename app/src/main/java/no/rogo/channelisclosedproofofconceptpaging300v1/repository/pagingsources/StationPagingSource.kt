package no.rogo.channelisclosedproofofconceptpaging300v1.repository.pagingsources

import android.util.Log
import androidx.paging.PagingSource
import no.rogo.channelisclosedproofofconceptpaging300v1.api.interfaces.APIFamappInterfaceService
import no.rogo.channelisclosedproofofconceptpaging300v1.api.responses.APIGetStationsResponse
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity
import retrofit2.HttpException
import java.io.IOException

/**
 * Created by Roar on 15.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
class StationPagingSource(
        private val apiFamappInterfaceService: APIFamappInterfaceService,
        private val userId: String,
        private val passfrase: String,
        private val latitude: String,
        private val longitude: String,
        private val lastVersion: String,
        private val killed: String,
        private val range: String
): PagingSource<Int, APIGetStationsResponse>() {

    val TAG = javaClass.simpleName

    override suspend fun load(
            params: LoadParams<Int>
    ): LoadResult<Int, APIGetStationsResponse>
    {
        val limit = params.loadSize.toString()
        val offset = (params.loadSize*(params.key?:1-1)).toString()
        val prevKey = if((params.key?:1-1)<=0) null else (params.key?:1-2)

        Log.i(TAG, "load: limit = $limit, offset = $offset, prevKey = $prevKey")
        try {
            val req = apiFamappInterfaceService.getStations(
                    userid = userId,
                    passfrase = passfrase,
                    latitude = latitude,
                    longitude = longitude,
                    limit = limit,
                    offset = offset,
                    lastversion = lastVersion,
                    killed = killed,
                    range = range
            )
            val nextKey = if(req.isEmpty()) null else (params.key?:0)+1
            Log.i(TAG, "load: nextKey = $nextKey")
            return LoadResult.Page(
                    data = req,
                    prevKey = prevKey,
                    nextKey = nextKey
            )

        }catch (exception: IOException)
        {
            Log.e(TAG,"load: LoadResult Error ",exception )
            return LoadResult.Error(exception)
        }catch (exception: HttpException)
        {
            Log.e(TAG,"load: LoadResult Http ",exception)
            return LoadResult.Error(exception)
        }
    }
}