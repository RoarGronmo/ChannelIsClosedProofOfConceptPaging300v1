package no.rogo.channelisclosedproofofconceptpaging300v1.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.PageNoResponse
import no.rogo.channelisclosedproofofconceptpaging300v1.room.responses.StationResponse

/**
 * Created by Roar on 13.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(stations: List<StationEntity>)

    @Query("""
        SELECT * FROM stations
    """)
    fun getPagedStationResponses(): PagingSource<Int,StationResponse>

    @Query("""
        SELECT 
            stationPrimaryKey,
            pageNo
        FROM 
            stations
    """)
    suspend fun getPageNoFromStationPrimaryKey(stationPrimaryKey: Long): PageNoResponse?

    @Query("""
        DELETE FROM stations
    """)
    suspend fun clearStations()

}