package com.jeanca.mapsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jeanca.mapsapp.models.AutocompletePlace

@Database(version = 1, entities = [AutocompletePlace::class])
abstract class SearchDatabase: RoomDatabase() {

    abstract fun searchDao(): SearchDao

    companion object {

        @Volatile
        private var instance: SearchDatabase? = null

        fun getDatabase(context: Context): SearchDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchDatabase::class.java, "searchedPlaces"
                ).build()
            }
            return instance!!
        }
    }
}
