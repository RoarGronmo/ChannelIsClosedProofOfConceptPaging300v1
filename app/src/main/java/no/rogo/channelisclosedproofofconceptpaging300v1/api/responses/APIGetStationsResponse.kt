package no.rogo.channelisclosedproofofconceptpaging300v1.api.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Roar on 13.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
class APIGetStationsResponse(
    @field: SerializedName("idsite") @field: Expose var idSite: String?,
    @field: SerializedName("stationname") @field:Expose var stationName: String?,
    @field: SerializedName("longitude") @field:Expose var longitude: String?,
    @field: SerializedName("latitude") @field:Expose var latitude: String?,
    @field: SerializedName("enterpriseid") @field:Expose var enterpriseId: String?,
    @field: SerializedName("googleplaceid") @field:Expose var googlePlaceId: String?,
    @field: SerializedName("stationaddress") @field:Expose var stationAddress: String?,
    @field: SerializedName("stationzip") @field:Expose var stationZip: String?,
    @field: SerializedName("stationplace") @field:Expose var stationPlace: String?,
    @field: SerializedName("countrycode") @field:Expose var countryCode: String?,
    @field: SerializedName("sitekilled") @field:Expose var siteKilled: String?,
    @field: SerializedName("datecreated") @field:Expose var dateCreated: String?,
    @field: SerializedName("stationcountry") @field:Expose var stationCountry: String?,


    )