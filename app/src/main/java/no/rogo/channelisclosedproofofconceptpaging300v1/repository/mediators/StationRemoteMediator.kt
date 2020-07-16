package no.rogo.channelisclosedproofofconceptpaging300v1.repository.mediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import no.rogo.channelisclosedproofofconceptpaging300v1.api.responses.APIGetStationsResponse
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity

/**
 * Created by Roar on 16.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@OptIn(ExperimentalPagingApi::class)
class StationRemoteMediator(

): RemoteMediator<Int, APIGetStationsResponse>(){

    override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, APIGetStationsResponse>
    ): MediatorResult
    {
        //TODO("Not yet implemented")

    }

}