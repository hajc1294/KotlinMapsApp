package com.jeanca.mapsapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import com.jeanca.mapsapp.api.ApiProvider
import com.jeanca.mapsapp.commons.Status
import com.jeanca.mapsapp.utils.JsonReader
import com.jeanca.mapsapp.viewmodels.DirectionsViewModel
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.HttpURLConnection

class DirectionsViewModelTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var directionsViewModel: DirectionsViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    init {
        val synchronousScheduler = Schedulers.from { obj: Runnable -> obj.run() }
        RxJavaPlugins.setNewThreadSchedulerHandler { synchronousScheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { synchronousScheduler }
    }

    @Before
    fun before() {
        mockWebServer = MockWebServer()
        ApiProvider.url = mockWebServer.url("/")
        directionsViewModel = DirectionsViewModel()
    }

    @After
    fun after() {
        mockWebServer.shutdown()
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun directionsRequestTest() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(JsonReader.readDataFromJsonFile("directions.json")))

        directionsViewModel.directionsRequest(
            LatLng(0.0, 0.0),
            LatLng(0.0, 0.0))

        Assert.assertEquals(directionsViewModel.getDirectionsStatus().value, Status.DONE)
        Assert.assertEquals(directionsViewModel.getDirections().count(), 1)
        Assert.assertEquals(directionsViewModel.getDistance(), "34.9 mi")
        Assert.assertEquals(directionsViewModel.getDuration(), "44 mins")
    }

    @Test
    fun directionsRequestErrorTest() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .setBody(JsonReader.readDataFromJsonFile("directions.json")))

        directionsViewModel.directionsRequest(
            LatLng(0.0, 0.0),
            LatLng(0.0, 0.0))

        Assert.assertEquals(directionsViewModel.getDirectionsStatus().value, Status.ERROR)
        Assert.assertEquals(directionsViewModel.getDistance(), "")
        Assert.assertEquals(directionsViewModel.getDuration(), "")
    }
}