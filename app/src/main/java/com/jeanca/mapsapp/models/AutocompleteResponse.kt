package com.jeanca.mapsapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AutocompleteResponse (
    @SerializedName("predictions") @Expose val predictions: List<AutocompletePlace>,
)

