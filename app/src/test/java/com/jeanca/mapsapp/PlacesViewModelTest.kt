package com.jeanca.mapsapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jeanca.mapsapp.api.ApiProvider
import com.jeanca.mapsapp.commons.Status
import com.jeanca.mapsapp.database.SearchDatabase
import com.jeanca.mapsapp.models.AutocompleteFormat
import com.jeanca.mapsapp.models.AutocompletePlace
import com.jeanca.mapsapp.utils.JsonReader
import com.jeanca.mapsapp.viewmodels.PlacesViewModel
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
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.net.HttpURLConnection

class PlacesViewModelTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var placesViewModel: PlacesViewModel

    @Mock
    private val mockContext: Context = mock(Context::class.java)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    init {
        val synchronousScheduler = Schedulers.from { obj: Runnable -> obj.run() }
        RxJavaPlugins.setNewThreadSchedulerHandler { synchronousScheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { synchronousScheduler }
    }

    @Before
    fun before() {
        `when`(mockContext.applicationContext).thenReturn(mockContext)

        mockWebServer = MockWebServer()
        ApiProvider.url = mockWebServer.url("/")

        val database = SearchDatabase.getDatabase(mockContext)
        placesViewModel = PlacesViewModel(database.searchDao())
    }

    @After
    fun after() {
        mockWebServer.shutdown()
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun autocompletePlacesRequestTest() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(JsonReader.readDataFromJsonFile("autocomplete.json")))

        placesViewModel.autocompletePlacesRequest("place")

        Assert.assertEquals(placesViewModel.getAutocompleteStatus().value, Status.DONE)
        Assert.assertEquals(placesViewModel.isEmpty(), false)
        Assert.assertEquals(placesViewModel.getLoadFromHistory(), false)
        Assert.assertEquals(placesViewModel.getAutocompletePlaces().count(), 5)

        val place = placesViewModel.getAutocompletePlaces()[0]
        Assert.assertEquals(place.placeId, "ChIJtRkkqIKyCVMRno6bQJpHqbA")
        Assert.assertEquals(place.description, "Alberta, Canada")
        Assert.assertEquals(place.structuredFormatting.mainText, "Alberta")
    }

    @Test
    fun placeDetailRequestTest() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(JsonReader.readDataFromJsonFile("detail.json")))

        val place = AutocompletePlace("id", "description",
            AutocompleteFormat("mainText", "secondaryText"))
        placesViewModel.placeDetailRequest(place)
        Assert.assertEquals(placesViewModel.getDetailStatus().value, Status.DONE)

        val detail = placesViewModel.getPlaceDetail()
        Assert.assertEquals(detail.name, "Alberta")
        Assert.assertEquals(detail.formattedAddress, "Alberta, Canada")
    }

    @Test
    fun autocompletePlacesRequestErrorTest() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .setBody(JsonReader.readDataFromJsonFile("autocomplete.json")))

        placesViewModel.autocompletePlacesRequest("place")

        Assert.assertEquals(placesViewModel.getAutocompleteStatus().value, Status.ERROR)
        Assert.assertEquals(placesViewModel.isEmpty(), true)
    }

    @Test
    fun placeDetailRequestErrorTest() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .setBody(JsonReader.readDataFromJsonFile("detail.json")))

        val place = AutocompletePlace("id", "description",
            AutocompleteFormat("mainText", "secondaryText"))
        placesViewModel.placeDetailRequest(place)
        Assert.assertEquals(placesViewModel.getDetailStatus().value, Status.ERROR)
    }
}