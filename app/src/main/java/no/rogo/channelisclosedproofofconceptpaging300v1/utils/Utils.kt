package no.rogo.channelisclosedproofofconceptpaging300v1.utils

import android.content.Context
import android.location.Location
import androidx.core.content.edit
import no.rogo.channelisclosedproofofconceptpaging300v1.R

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

    fun getLocationTrackingPref(context: Context): Boolean = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),Context.MODE_PRIVATE).getBoolean(
        KEY_FOREGROUND_ENABLED, false)


    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }

}