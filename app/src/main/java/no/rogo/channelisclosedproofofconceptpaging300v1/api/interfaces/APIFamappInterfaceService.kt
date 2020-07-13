package no.rogo.channelisclosedproofofconceptpaging300v1.api.interfaces

import no.rogo.channelisclosedproofofconceptpaging300v1.api.responses.APIGetStationsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Roar on 13.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */

interface APIFamappInterfaceService {

    @GET("/fuelpump2/liststations_fp3_v6.php")
    suspend fun getStations(
        @Query("userid") userid: String,
        @Query("passfrase") passfrase: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("limit") limit: String,
        @Query("offset") offset: String,
        @Query("lastversion") lastversion: String,
        @Query("killed") killed: String,
        @Query("range") range: String,
    ):List<APIGetStationsResponse>


}