package com.example.acar.order


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.acar.databinding.OrderFragmentBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.AndroidEntryPoint
import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.acar.R
import com.example.acar.common.AppModule
import com.example.acar.common.GlobalToast
import com.example.acar.common.GoogleApiRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException


@AndroidEntryPoint
class OrderFragment() : Fragment() {

    private lateinit var viewModel: OrderViewModel
    private var _binding: OrderFragmentBinding? = null
    private val binding get() = _binding!!
    private var supportMapFragment: SupportMapFragment? = null
    private lateinit var geoCoder: Geocoder
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.testNavToPost.setOnClickListener {
            navController.navigate(R.id.action_orderFragment_to_postOrderFragment)
        }

//developing branch
        binding.orderButton.setOnClickListener {
            if (setStringPickupAndDestinationAddress()) {
                viewModel.getLatLngFromAddresses(geoCoder)
                viewModel.hasResults.observe(viewLifecycleOwner){hasResults ->
                    if (!hasResults){
                        GlobalToast.showShort(context, "No results")
                        viewModel.doneShowingNoResultsToast()
                    }
                }
                viewModel.destinationLatLng.observe(viewLifecycleOwner) {
                    if (it != null) {
                        viewModel.generatePickupAndDestinationMarkers()
                    }
                }
                createPickupAndDestinationMarkers()
                observeAndCreatePolyLines()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModelFactory = OrderViewModelFactory(googleApiRepository = GoogleApiRepository())
        navController = findNavController()
        val store = navController.getViewModelStoreOwner(R.id.nav_graph_order)
        viewModel = ViewModelProvider(store,viewModelFactory)[OrderViewModel::class.java]
        _binding = OrderFragmentBinding.inflate(layoutInflater)
        supportMapFragment = childFragmentManager.findFragmentById(com.example.acar.R.id.map) as SupportMapFragment?
        geoCoder = Geocoder(context)
        return binding.root
    }

    private fun getLocationFromAddress(context: Context, strAddress: String): LatLng? {

        val addressList: List<Address>
        var p1: LatLng? = null
        try {

            addressList = geoCoder.getFromLocationName(strAddress, 5)

            if (addressList.isEmpty()) {
                Log.d("orderfragment", addressList.toString())
                return null

            }
            val location = addressList[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        return p1
    }



    private fun createPickupAndDestinationMarkers() {
        var position: LatLng
        val latLngBuilder = LatLngBounds.Builder()
        var bounds: LatLngBounds?
        viewModel.pickupAndDestinationMarkers.observe(viewLifecycleOwner) { markerOptions ->
            supportMapFragment?.getMapAsync { googleMap ->
                googleMap.clear()
                if (markerOptions.isNotEmpty()) {
                    markerOptions.forEach { marker ->
                        position = marker.position
                        latLngBuilder.include(LatLng(position.latitude, position.longitude))
                        googleMap.addMarker(marker)
                    }
                    bounds = latLngBuilder.build()
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds!!, 200))
                }
            }
            viewModel.getPolylineLatLngs()
        }

    }

    private fun observeAndCreatePolyLines(){
        viewModel.polyLinesLatLng.observe(viewLifecycleOwner){ polyLineLatLngs ->
            val polylineOptions = PolylineOptions ()
            supportMapFragment?.getMapAsync { googleMap ->
                if (polyLineLatLngs != null) {
                    for (latLng in polyLineLatLngs){
                        polylineOptions.add(latLng)
                    }
                }
                googleMap.addPolyline(polylineOptions.width(5F).color(Color.BLUE))
            }
        }
    }


    private fun setStringPickupAndDestinationAddress(): Boolean {
        val pickupAddress = binding.editTextPickup.text.toString()
        val destinationAddress = binding.editTextDestination.text.toString()
        if (pickupAddress.isNotEmpty() && destinationAddress.isNotEmpty() && pickupAddress!=destinationAddress) {
            viewModel.setPickupAndDestinationAddress(pickupAddress, destinationAddress)
            return true
        }
        else {
            GlobalToast.showShort(context, "Address is empty")
            return false
        }
    }
}