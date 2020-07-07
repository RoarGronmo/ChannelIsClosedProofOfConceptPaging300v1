package no.rogo.channelisclosedproofofconceptpaging300v1.utils

import android.location.Location

/**
 * Created by Roar on 01.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */

fun Location?.toText():String{
    return if (this != null)
    {
        "($latitude, $longitude)"
    }
    else
    {
        "Uknown location"
    }
}

internal object SharedPreferenceUtil{
    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
}