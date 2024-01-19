package com.jeanca.mapsapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jeanca.mapsapp.api.ApiProvider
import com.jeanca.mapsapp.commons.Constants
import com.jeanca.mapsapp.commons.Status
import com.jeanca.mapsapp.database.SearchDao
import com.jeanca.mapsapp.models.AutocompletePlace
import com.jeanca.mapsapp.models.PlaceDetail
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PlacesViewModel(private val searchDao: SearchDao) {

    private val tag: String = "PlacesViewModel"
    private val compositeDisposable: CompositeDisposable =  CompositeDisposable()
    private val autocompleteStatus: MutableLiveData<Status> = MutableLiveData()
    private val detailStatus: MutableLiveData<Status> = MutableLiveData()
    private val autocompletePlaces: MutableLiveData<List<AutocompletePlace>> = MutableLiveData()
    private val placeResult: MutableLiveData<PlaceDetail> = MutableLiveData()
    private var loadFromHistory: Boolean = true

    fun getAutocompleteStatus(): LiveData<Status> = autocompleteStatus
    fun getDetailStatus(): LiveData<Status> = detailStatus
    fun getAutocompletePlaces(): List<AutocompletePlace> = autocompletePlaces.value ?: listOf()
    fun getPlaceDetail(): PlaceDetail = placeResult.value!!

    fun isEmpty(): Boolean = autocompletePlaces.value.isNullOrEmpty()
    fun getLoadFromHistory(): Boolean = loadFromHistory

    fun autocompletePlacesRequest(placeName: String) {
        autocompleteStatus.value = Status.LOADING

        if (placeName.isEmpty()) {
            getPlacesHistory()
        } else {
            compositeDisposable.add(
                ApiProvider.provider()
                    .getPlaceAutocomplete(placeName, Constants.API_KEY)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        loadFromHistory = false
                        autocompletePlaces.value = it.predictions
                        autocompleteStatus.value = Status.DONE
                    }, {
                        it.printStackTrace()
                        autocompleteStatus.value = Status.ERROR
                    })
            )
        }
    }

    fun placeDetailRequest(autocompletePlace: AutocompletePlace) {
        detailStatus.value = Status.LOADING
        compositeDisposable.add(
            ApiProvider.provider()
                .getPlaceDetail(autocompletePlace.placeId, Constants.API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    placeResult.value = it.result
                    saveSearchedPlace(autocompletePlace)
                    detailStatus.value = Status.DONE
                }, {
                    it.printStackTrace()
                    detailStatus.value = Status.ERROR
                })
        )
    }

    fun getPlacesHistory() {
        detailStatus.value = Status.LOADING
        compositeDisposable.add(
            searchDao.getAllPlaces()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    loadFromHistory = true
                    autocompletePlaces.value = it ?: listOf()
                    autocompleteStatus.value = Status.DONE
                }, {
                    it.printStackTrace()
                    autocompleteStatus.value = Status.ERROR
                })
        )
    }

    fun clearHistory() {
        detailStatus.value = Status.LOADING
        compositeDisposable.add(
            searchDao.deleteAllPlaces()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    Log.d(tag, "Clean success. Code: $it")
                    autocompletePlaces.value = listOf()
                    autocompleteStatus.value = Status.DONE
                }, {
                    it.printStackTrace()
                    autocompleteStatus.value = Status.ERROR
                })
        )
    }

    private fun saveSearchedPlace(autocompletePlace: AutocompletePlace?) {
        if (autocompletePlace != null) {
            compositeDisposable.add(
                searchDao.insertPlace(autocompletePlace)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        Log.d(tag, "Save success")
                    }, {
                        it.printStackTrace()
                    })
            )
        }
    }
}