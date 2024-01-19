package com.jeanca.mapsapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlaceResponse (
    @SerializedName("result") @Expose val result: PlaceDetail
)