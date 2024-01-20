package com.jeanca.mapsapp.viewcontrollers

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeanca.mapsapp.R
import com.jeanca.mapsapp.commons.Constants.PLACE_DETAIL
import com.jeanca.mapsapp.commons.Status
import com.jeanca.mapsapp.database.SearchDatabase
import com.jeanca.mapsapp.databinding.ActivitySearchPlacesBinding
import com.jeanca.mapsapp.models.AutocompletePlace
import com.jeanca.mapsapp.viewcontrollers.adapters.PlacesAdapter
import com.jeanca.mapsapp.viewcontrollers.adapters.PlacesAdapterCallback
import com.jeanca.mapsapp.viewmodels.PlacesViewModel

class SearchPlacesActivity: AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var binding: ActivitySearchPlacesBinding
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var placesViewModel: PlacesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = SearchDatabase.getDatabase(applicationContext)
        placesViewModel = PlacesViewModel(database.searchDao())
        placesViewModel.getPlacesHistory()

        observeLiveData()
        searchPlaceListener()
        setOnClickListeners()
    }

    private fun observeLiveData() {
        placesViewModel.getAutocompleteStatus().observe(this) {
            when (it) {
                Status.LOADING -> {
                    binding.emptyLabel.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                } Status.DONE -> {
                    loadSearchedPlaces()
                } else -> {
                    Toast.makeText(applicationContext,
                        resources.getString(R.string.request_places_error), Toast.LENGTH_SHORT)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        placesViewModel.getDetailStatus().observe(this) {
            when (it) {
                Status.LOADING -> {
                    binding.emptyLabel.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                } Status.DONE -> {
                    val intent = Intent()
                    intent.putExtra(PLACE_DETAIL, placesViewModel.getPlaceDetail())
                    setResult(RESULT_OK, intent)
                    finish()
                } else -> {
                    Toast.makeText(applicationContext,
                        resources.getString(R.string.request_places_error), Toast.LENGTH_SHORT)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun loadSearchedPlaces() {
        if (binding.clearHistoryLabel.isVisible) {
            binding.clearHistoryLabel.visibility = View.GONE
        }

        if (placesViewModel.isEmpty()) {
            binding.recyclerCardContainer.visibility = View.GONE
            binding.stateLayout.visibility = View.VISIBLE
            binding.emptyLabel.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.recyclerCardContainer.visibility = View.VISIBLE
            binding.stateLayout.visibility = View.GONE

            if (placesViewModel.getLoadFromHistory()) {
                binding.clearHistoryLabel.visibility = View.VISIBLE
            }
            setPlacesAdapter()
        }
    }

    private fun setPlacesAdapter() {
        placesAdapter = PlacesAdapter(placesViewModel.getAutocompletePlaces(),
            placesViewModel.getLoadFromHistory(),
            object : PlacesAdapterCallback {
            override fun onPlaceSelected(autocompletePlace: AutocompletePlace) {
                placesViewModel.placeDetailRequest(autocompletePlace)
            }
        })
        binding.placesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = placesAdapter
        }
    }

    private fun searchPlaceListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                searchPlaceDelay(p0.toString())
            }
        })
    }

    private fun searchPlaceDelay(searchText: String) {
        if (this::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
        runnable = Runnable {
            placesViewModel.autocompletePlacesRequest(searchText)
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun setOnClickListeners() {
        binding.backImage.setOnClickListener {
            finish()
        }
        binding.cleanSearchImage.setOnClickListener {
            binding.searchEditText.text.clear()
        }
        binding.clearHistoryLabel.setOnClickListener {
            placesViewModel.clearHistory()
        }
    }
}