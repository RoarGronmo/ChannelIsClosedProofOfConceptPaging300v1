package no.rogo.channelisclosedproofofconceptpaging300v1.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.DeviceLocationEntity

/**
 * Created by Roar on 17.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@Dao
interface SearchLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(deviceLocationEntity: DeviceLocationEntity)
}