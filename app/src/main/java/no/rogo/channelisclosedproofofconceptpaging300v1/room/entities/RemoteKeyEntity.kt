package no.rogo.channelisclosedproofofconceptpaging300v1.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Roar on 17.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */

@Entity (tableName = "remotekeys")
data class RemoteKeyEntity(
        @PrimaryKey val remoteKeyId: Long,
        val stationId: String,
        val prevKey: Int?,
        val nextKey: Int?
)