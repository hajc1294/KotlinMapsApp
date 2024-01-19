package com.jeanca.mapsapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class PlaceDetail (
    @SerializedName("formatted_address") @Expose val formattedAddress: String,
    @SerializedName("geometry") @Expose val geometry: PlaceGeometry,
    @SerializedName("name") @Expose val name: String
) : Parcelable

@Parcelize
class PlaceGeometry (
    @SerializedName("location") @Expose val location: PlaceLocation,
): Parcelable

@Parcelize
data class PlaceLocation (
    @SerializedName("lat") @Expose val lat: Double,
    @SerializedName("lng") @Expose val lng: Double,
): Parcelable