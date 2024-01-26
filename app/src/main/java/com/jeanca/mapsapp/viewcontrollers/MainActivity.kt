package com.jeanca.mapsapp.viewcontrollers

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.jeanca.mapsapp.R
import com.jeanca.mapsapp.commons.Constants.PLACE_DETAIL
import com.jeanca.mapsapp.commons.Status
import com.jeanca.mapsapp.databinding.ActivityMainBinding
import com.jeanca.mapsapp.models.PlaceDetail
import com.jeanca.mapsapp.utils.BitmapUtils
import com.jeanca.mapsapp.utils.strPoint
import com.jeanca.mapsapp.viewmodels.DirectionsViewModel
import com.jeanca.mapsapp.viewmodels.UserViewModel

class MainActivity: AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private val tag: String = "MainActivity"
    private val userViewModel: UserViewModel = UserViewModel()
    private val directionsViewModel: DirectionsViewModel = DirectionsViewModel()
    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionRequestStatus()
        observeLiveData()
        searchEventListener()
        setOnClickListeners()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
            )
        } catch (e: Resources.NotFoundException) {
            Log.e(tag, "Can't find style. Error: ", e)
        }

        val fineLocationPermission = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineLocationPermission && coarseLocationPermission) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
            locationManager = getSystemService(Service.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,
                10f, this)

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                userViewModel.setCurrentLocation(location)
                updateMapCamera()
            } else {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(9.888946, -84.108610),
                        7f
                    )
                )
            }
        }

        mMap.setOnMapClickListener {
            cleanMapData()
            val currentLocation = userViewModel.getCurrentLocation()
            binding.placeName.text = resources.getString(R.string.custom_location)
            binding.placeDescription.text = currentLocation.strPoint(true)
            directionsViewModel.directionsRequest(userViewModel.getCurrentLocation(), it)
            addMapMarker(it)
        }
    }

    override fun onLocationChanged(p0: Location) {
        userViewModel.setCurrentLocation(p0)
    }

    private fun permissionRequestStatus() {
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 1
        )

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) ==
            PackageManager.PERMISSION_GRANTED) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    private fun observeLiveData() {
        directionsViewModel.getDirectionsStatus().observe(this) {
            when (it) {
                Status.LOADING -> {
                    print("")
                } Status.DONE -> {
                    loadRouteInMap()
                } else -> {
                    Toast.makeText(applicationContext,
                        resources.getString(R.string.request_directions_error), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun searchEventListener() {
        val startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val place = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data?.getParcelableExtra(PLACE_DETAIL, PlaceDetail::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    data?.getParcelableExtra(PLACE_DETAIL)!!
                }

                if (place != null) {
                    loadSearchedPlace(place)
                }
            }
        }
        binding.searchLabel.setOnClickListener {
            startForResult.launch(Intent(this, SearchPlacesActivity::class.java))
        }
    }

    private fun setOnClickListeners() {
        binding.clearMapFab.setOnClickListener {
            cleanMapData()
        }
        binding.myLocationFab.setOnClickListener {
            updateMapCamera(animated = true)
        }
    }
    
    private fun loadSearchedPlace(placeResult: PlaceDetail) {
        cleanMapData()
        binding.placeName.text = placeResult.name
        binding.placeDescription.text = placeResult.formattedAddress

        val placeLocation = LatLng(placeResult.geometry.location.lat,
            placeResult.geometry.location.lng)
        directionsViewModel.directionsRequest(userViewModel.getCurrentLocation(), placeLocation)
        addMapMarker(placeLocation)
        updateMapCamera(newLocation = placeLocation, animated = true)
    }

    private fun loadRouteInMap() {
        val points = directionsViewModel.getPoints()

        if (points.isEmpty()) {
            Toast.makeText(applicationContext,
                resources.getString(R.string.request_directions_error), Toast.LENGTH_SHORT).show()
        } else {
            val polylineOptions = PolylineOptions()
            loadRouteData()

            polylineOptions.addAll(points)
            polylineOptions.width(8f)
            polylineOptions.color(Color.WHITE)
            mMap.addPolyline(polylineOptions)
        }
    }

    private fun loadRouteData() {
        binding.routeLayout.visibility = View.VISIBLE
        binding.durationLabel.text = directionsViewModel.getDuration()
        binding.distanceLabel.text = directionsViewModel.getDistance()
    }

    private fun updateMapCamera(newLocation: LatLng? = null, animated: Boolean = false) {
        val location = newLocation ?: userViewModel.getCurrentLocation()
        val camera = CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,
            location.longitude), 13f)

        if (animated) {
            mMap.animateCamera(camera)
        } else {
            mMap.moveCamera(camera)
        }
    }

    private fun addMapMarker(place: LatLng) {
        mMap.addMarker(
            MarkerOptions().position(place).title(resources.getString(R.string.finish))
                .icon(BitmapUtils.bitmapDescriptorFromVector(this,
                    R.drawable.baseline_place_35))
        )
    }

    private fun cleanMapData() {
        mMap.clear()
        binding.placeName.text = resources.getString(R.string.base_location_name_default)
        binding.placeDescription.text = resources.getString(R.string.base_location_desc_default)
        binding.routeLayout.visibility = View.GONE
    }
}