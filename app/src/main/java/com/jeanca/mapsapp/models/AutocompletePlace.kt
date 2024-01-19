package com.jeanca.mapsapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class AutocompletePlace (
    @PrimaryKey
    @SerializedName("place_id") @Expose val placeId: String,
    @SerializedName("description") @Expose val description: String,
    @Embedded
    @SerializedName("structured_formatting") @Expose val structuredFormatting: AutocompleteFormat
)

@Entity
class AutocompleteFormat (
    @SerializedName("main_text") @Expose val mainText: String,
    @SerializedName("secondary_text") @Expose val secondaryText: String
)