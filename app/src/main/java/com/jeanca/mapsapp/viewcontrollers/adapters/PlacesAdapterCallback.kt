package com.jeanca.mapsapp.viewcontrollers.adapters

import com.jeanca.mapsapp.models.AutocompletePlace

interface PlacesAdapterCallback {
    fun onPlaceSelected(autocompletePlace: AutocompletePlace)
}