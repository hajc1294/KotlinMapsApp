package com.jeanca.mapsapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeanca.mapsapp.models.AutocompletePlace
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface SearchDao {

    @Query("SELECT * FROM AutocompletePlace")
    fun getAllPlaces(): Single<List<AutocompletePlace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(autocompletePlace: AutocompletePlace): Completable

    @Query("DELETE FROM AutocompletePlace")
    fun deleteAllPlaces(): Single<Int>
}