package com.jeanca.mapsapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.jeanca.mapsapp.api.ApiProvider
import com.jeanca.mapsapp.commons.Constants
import com.jeanca.mapsapp.commons.Status
import com.jeanca.mapsapp.models.Routes
import com.jeanca.mapsapp.utils.strPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DirectionsViewModel {

    private val compositeDisposable: CompositeDisposable =  CompositeDisposable()
    private val directionsStatus: MutableLiveData<Status> = MutableLiveData()
    private val directions: MutableLiveData<List<Routes>> = MutableLiveData()

    fun getDirectionsStatus(): LiveData<Status> = directionsStatus
    fun getDirections(): List<Routes> = directions.value ?: listOf()

    fun directionsRequest(origin: LatLng, destination: LatLng) {
        directionsStatus.value = Status.LOADING
        compositeDisposable.add(
            ApiProvider.provider()
                .getDirections(origin.strPoint(), destination.strPoint(),
                    Constants.API_KEY, false)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    directions.value = it.routes
                    directionsStatus.value = Status.DONE
                }, {
                    it.printStackTrace()
                    directionsStatus.value = Status.ERROR
                })
        )
    }

    fun getDistance(): String =
        if (directions.value != null)
            directions.value!![0].legs[0].distance.text
        else String()

    fun getDuration(): String =
        if (directions.value != null)
             directions.value!![0].legs[0].duration.text
        else String()

    fun getPoints(): List<LatLng> {
        return if (getDirections().isEmpty()) {
            listOf()
        } else {
            PolyUtil.decode(getDirections()[0].overviewPolyline.points)
        }
    }
}