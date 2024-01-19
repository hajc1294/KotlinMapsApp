package com.jeanca.mapsapp

import android.content.Context
import android.location.Location
import com.jeanca.mapsapp.viewmodels.UserViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class UserViewModelTest {

    private lateinit var userViewModel: UserViewModel

    @Mock
    private val mockLocation: Location = mock(Location::class.java)

    @Before
    fun before() {
        userViewModel = UserViewModel()
    }

    @Test
    fun currentLocationTest() {
        userViewModel.setCurrentLocation(mockLocation)

        val userLocation = userViewModel.getCurrentLocation()
        Assert.assertEquals(userLocation.latitude.toLong(), 0)
        Assert.assertEquals(userLocation.longitude.toLong(), 0)
    }
}