package com.jeanca.mapsapp.api

import com.jeanca.mapsapp.models.AutocompleteResponse
import com.jeanca.mapsapp.models.DirectionsResponse
import com.jeanca.mapsapp.models.PlaceResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("place/autocomplete/json")
    fun getPlaceAutocomplete(
        @Query("input") input: String,
        @Query("key") key: String,
    ): Observable<AutocompleteResponse>

    @GET("place/details/json")
    fun getPlaceDetail(
        @Query("place_id") placeId: String,
        @Query("key") key: String,
    ): Observable<PlaceResponse>

    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String,
        @Query("sensor") sensor: Boolean,
    ): Observable<DirectionsResponse>
}