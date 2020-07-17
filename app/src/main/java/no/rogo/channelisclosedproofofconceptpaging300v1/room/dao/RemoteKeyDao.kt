package no.rogo.channelisclosedproofofconceptpaging300v1.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.RemoteKeyEntity

/**
 * Created by Roar on 17.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeyEntity>)

    @Query("""
        SELECT * 
        FROM remotekeys 
        WHERE remoteKeyId = :remoteKeyId
    """)
    suspend fun getRemoteKeysFromRemoteKeyId(remoteKeyId: Long):RemoteKeyEntity

    @Query("""
        SELECT * 
            FROM remotekeys
            WHERE stationId = :stationId
    """)
    suspend fun getRemoteKeysFromStationId(stationId: String):RemoteKeyEntity

    @Query("""
        DELETE FROM remotekeys
    """)
    suspend fun clearRemoteKeys()
}