package no.rogo.channelisclosedproofofconceptpaging300v1.repository.pagingsources

import androidx.paging.PagingSource
import no.rogo.channelisclosedproofofconceptpaging300v1.api.interfaces.APIFamappInterfaceService
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity

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
        private val limit: String,
        private val offset: String,
        private val lastversion: String,
        private val killed: String,
        private val range: String
): PagingSource<Int, StationEntity>() {
    override suspend fun load(
            params: LoadParams<Int>
    ): LoadResult<Int, StationEntity>
    {
        val req = apiFamappInterfaceService.getStations(

        )
    }
}