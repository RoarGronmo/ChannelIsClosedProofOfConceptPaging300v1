package no.rogo.channelisclosedproofofconceptpaging300v1.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by Roar on 15.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
@Entity(tableName = "devicelocation")
data class DeviceLocationEntity (
        @PrimaryKey var deviceLocationPrimaryKey:Long,
        var latitude: Double,
        var longitude: Double,
        var time:Long
)