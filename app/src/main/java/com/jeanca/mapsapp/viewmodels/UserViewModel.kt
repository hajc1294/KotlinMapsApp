package com.jeanca.mapsapp.viewmodels

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.jeanca.mapsapp.models.UserData

class UserViewModel() {
    private var userData: UserData = UserData()

    fun setCurrentLocation(location: Location) {
        userData.currentLocation = LatLng(location.latitude, location.longitude)
    }

    fun getCurrentLocation(): LatLng {
        return userData.currentLocation
    }
}