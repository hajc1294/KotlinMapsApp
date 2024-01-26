package com.jeanca.mapsapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DirectionsResponse(
    @SerializedName("routes") @Expose val routes: List<Routes>
)

class Routes (
    @SerializedName("legs") @Expose val legs: List<Legs>,
    @SerializedName("overview_polyline") @Expose val overviewPolyline: Polyline
)

class Legs (
    @SerializedName("distance") @Expose val distance: Data,
    @SerializedName("duration") @Expose val duration: Data
)

class Data (
    @SerializedName("text") @Expose val text: String
)

class Polyline (
    @SerializedName("points") @Expose val points: String
)

