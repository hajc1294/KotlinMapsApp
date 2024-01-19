package com.jeanca.mapsapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DirectionsResponse(
    @SerializedName("routes") @Expose val routes: List<Routes>
)

class Routes (
    @SerializedName("legs") @Expose val legs: List<Legs>
)

class Legs (
    @SerializedName("distance") @Expose val distance: Distance,
    @SerializedName("duration") @Expose val duration: Duration,
    @SerializedName("steps") @Expose val steps: List<Steps>
)

class Steps (
    @SerializedName("distance") @Expose val distance: Distance,
    @SerializedName("duration") @Expose val duration: Duration,
    @SerializedName("polyline") @Expose val polyline: Polyline
)

class Distance (
    @SerializedName("text") @Expose val text: String
)

class Duration (
    @SerializedName("text") @Expose val text: String
)

class Polyline (
    @SerializedName("points") @Expose val points: String
)

