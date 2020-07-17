package no.rogo.channelisclosedproofofconceptpaging300v1.room.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import no.rogo.channelisclosedproofofconceptpaging300v1.room.dao.DeviceLocationDao
import no.rogo.channelisclosedproofofconceptpaging300v1.room.dao.RemoteKeyDao
import no.rogo.channelisclosedproofofconceptpaging300v1.room.dao.SearchLocationDao
import no.rogo.channelisclosedproofofconceptpaging300v1.room.dao.StationDao
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.DeviceLocationEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.RemoteKeyEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.SearchLocationEntity
import no.rogo.channelisclosedproofofconceptpaging300v1.room.entities.StationEntity

/**
 * Created by Roar on 13.07.2020.
 * Copyright RoGo Software / Gronmo IT
 */

@Database(entities = [
    StationEntity::class,
    DeviceLocationEntity::class,
    SearchLocationEntity::class,
    RemoteKeyEntity::class],
    version = 1,
    exportSchema = false)
public abstract class AppDatabase:RoomDatabase()
{
    abstract fun stationDao(): StationDao
    abstract fun deviceLocationDao(): DeviceLocationDao
    abstract fun searchLocationDao(): SearchLocationDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    val TAG = javaClass.simpleName

    companion object{
        val TAG = this::class.java.simpleName
        @Volatile private var instance: AppDatabase?=null

        fun getInstance(context: Context): AppDatabase
        {
            return instance?: synchronized(this)
            {
                instance ?:buildDatabase(context).also{instance = it}
            }
        }

        private fun buildDatabase(context: Context):AppDatabase
        {
            Log.i(TAG, "buildDatabase: () !")
            return Room
                .databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "room_proofofconcept")
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        Log.i(TAG, "companion buildDatabase onCreate: ()")
                        super.onCreate(db)
                    }
                }).build()
        }
    }



}