package no.rogo.channelisclosedproofofconceptpaging300v1.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Roar on 17.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@Entity(tableName = "searchlocation")
class SearchLocationEntity (
        @PrimaryKey var primaryKey: Int,
        var latitude: Double?=null,
        var longitude: Double?=null,
        var time:Long?=null
)