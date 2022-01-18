package com.example.acar.ordersHistory

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.acar.BuildConfig
import com.example.acar.R
import com.example.acar.databinding.ItemHistoryOrderBinding
import com.example.acar.order.OrderViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.log

class HistoryAdapter(private val historyOfRides: ArrayList<RideHistoryItem>, private val viewModel: OrderViewModel) :
    ListAdapter<RideHistoryItem, HistoryAdapter.ViewHolder>(HistoryDiffCallback()) {

    class HistoryDiffCallback : DiffUtil.ItemCallback<RideHistoryItem>() {
        override fun areItemsTheSame(oldItem: RideHistoryItem, newItem: RideHistoryItem): Boolean {
            return oldItem.polyLineOverview == newItem.polyLineOverview
        }

        override fun areContentsTheSame(oldItem: RideHistoryItem, newItem: RideHistoryItem): Boolean {
            return oldItem.polyLineOverview == newItem.polyLineOverview
        }
    }

    inner class ViewHolder(private val itemBinding: ItemHistoryOrderBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(ride: RideHistoryItem) {
            itemBinding.dateTv.text = ride.date
            itemBinding.pickupTv.text = ride.pickup
            itemBinding.destinationTv.text = ride.destination
//            val pickupLatLng = ride.pickupLatLng
//                    .toString()
//                    .replace("()", "")
//            val destinationLatLng = ride.destinationLatLng
//                    .toString()
//                    .replace("()", "")
//              "https://maps.googleapis.com/maps/api/staticmap?size=400x200&markes=" + pickupLatLng + "|" + destinationLatLng + "&path=weight:3|color:blue|enc:"
            val encodedPolyLine = ride.polyLineOverview
            val baseGoogleUrl =
                "https://maps.googleapis.com/maps/api/staticmap?size=400x200&markes=&path=weight:3|color:blue|enc:"
            val formattedUrl = baseGoogleUrl + encodedPolyLine.replace("\\\\", "\\") + "&key=" + BuildConfig.MAPS_API_KEY

            Log.d("HistoryAdapter formattedurl:", formattedUrl)
            Glide
                    .with(itemBinding.root)
                    .load(formattedUrl)
                    .centerInside()
                    .into(itemBinding.mapImage)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // TODO: 17.01.2022  history of rides in viewmodel as alist   historyOfRides = viewmodle.list. so its initialised early
        submitList(historyOfRides)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rideItem = getItem(position)
        holder.bind(rideItem)
    }
}