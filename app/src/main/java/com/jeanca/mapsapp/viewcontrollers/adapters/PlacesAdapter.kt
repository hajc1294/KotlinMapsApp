package com.jeanca.mapsapp.viewcontrollers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jeanca.mapsapp.R
import com.jeanca.mapsapp.databinding.ItemPlacesListBinding
import com.jeanca.mapsapp.models.AutocompletePlace

class PlacesAdapter(private val placesList: List<AutocompletePlace>,
                    private val historyMode: Boolean,
                    private val placesAdapterCallback: PlacesAdapterCallback):
    RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemPlacesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder) {
            with (placesList[position]) {
                binding.placeTitleLabel.text = this.structuredFormatting.mainText
                binding.placeDescLabel.text = this.structuredFormatting.secondaryText

                if (historyMode) {
                    binding.itemMainImage.setImageResource(R.drawable.baseline_history_35)
                    binding.itemActionImage.visibility = View.GONE
                } else {
                    binding.itemMainImage.setImageResource(R.drawable.baseline_place_35)
                    binding.itemActionImage.visibility = View.VISIBLE
                }

                if (position == placesList.count() - 1) {
                    binding.placeDivider.visibility = View.GONE
                }

                itemView.setOnClickListener {
                    placesAdapterCallback.onPlaceSelected(this)
                }
            }
        }
    }

    override fun getItemCount(): Int = placesList.size

    inner class ViewHolder(var binding: ItemPlacesListBinding) :
        RecyclerView.ViewHolder(binding.root)
}