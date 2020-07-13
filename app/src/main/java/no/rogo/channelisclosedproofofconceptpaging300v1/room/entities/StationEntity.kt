package no.rogo.channelisclosedproofofconceptpaging300v1.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Roar on 13.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey var stationId: String,
    var stationName: String?=null,
    var latitude: Float?=null,
    var longitude: Float?=null,
    var airDistance: Float?=null,
    var enterpriseId: Int?=null,
    var killed: Boolean?=null
)
