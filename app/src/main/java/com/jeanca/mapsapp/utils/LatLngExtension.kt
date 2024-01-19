package com.jeanca.mapsapp.utils

import com.google.android.gms.maps.model.LatLng
import com.jeanca.mapsapp.commons.Constants.FLOAT_FORMAT

fun LatLng.strPoint(withSpace: Boolean = false): String {
    return if (withSpace) {
        "${FLOAT_FORMAT.format(this.latitude)}, ${FLOAT_FORMAT.format(this.longitude)}"
    } else {
        "${this.latitude},${this.longitude}"
    }
}
