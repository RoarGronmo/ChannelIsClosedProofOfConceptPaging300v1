package no.rogo.channelisclosedproofofconceptpaging300v1.api.factories

import android.util.Log
import no.rogo.channelisclosedproofofconceptpaging300v1.api.interfaces.APIFamappInterfaceService
import okhttp3.OkHttpClient
import retrofit2.Retrofit.Builder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Roar on 13.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */
object APIFamappClientFactory {
    val TAG = this::class.java.simpleName

    private var apiFamappInterfaceService: APIFamappInterfaceService?= null

    fun makeAPIFamappInterfaceService(): APIFamappInterfaceService{

        if(apiFamappInterfaceService == null)
        {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            apiFamappInterfaceService = Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(APIFamappInterfaceService::class.java)

            Log.i(TAG, "makeAPIFamappInterfaceService: ")

        }
        return apiFamappInterfaceService!!
    }


}