package com.example.acar.order


import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.acar.R
import com.example.acar.common.*
import com.example.acar.databinding.OrderFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.*


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

        setToolbarOnMenuClickListener()
        orderBtnOnClick()
        cancelBtnOnClick()
        carHereBtnOnClick()
    }

    private fun setToolbarOnMenuClickListener() {
        binding.orderToolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
    }

    private fun carHereBtnOnClick() {
        binding.carIsHereBtm.setOnClickListener {
            navController.navigate(R.id.action_orderFragment_to_postOrderFragment)
        }
    }

    private fun cancelBtnOnClick() {
        binding.cancelOrderBtn.setOnClickListener {
            binding.groupCarArrival.visibility = View.GONE
            binding.groupInitialViewsOrder.visibility = View.VISIBLE
            viewModel.cancelAllCoroutineJobs()
            supportMapFragment?.getMapAsync { googleMap ->
                googleMap.clear()
            }
            GlobalToast.showShort(context, "You've cancelled your order")
        }
    }

    private fun orderBtnOnClick() {
        binding.orderButton.setOnClickListener {
            viewModel.clearPickupAndDestinationLatLngs()
            if (setStringPickupAndDestinationAddress()) {
                viewModel.getLatLngFromAddresses(geoCoder)
                viewModel.hasResults.observe(viewLifecycleOwner) { hasResults ->
                    if (!hasResults) {
                        GlobalToast.showShort(context, "No results")
                        //    viewModel.doneShowingNoResultsToast()
                    }
                    else {
                        viewModel.getDirectionsResponse()
                        viewModel.directionsResponse.observe(viewLifecycleOwner) { response ->
                            if (response != null) {
                                viewModel.destinationLatLng.observe(viewLifecycleOwner) {
                                    if (it.latitude != 0.0) {
                                        viewModel.generatePickupAndDestinationMarkers()
                                        createPickupAndDestinationMarkers()
                                        observeAndCreatePolyLines()
                                    }
                                    binding.groupInitialViewsOrder.visibility = View.GONE
                                    binding.groupCarArrival.visibility = View.VISIBLE
                                    setCarArrivalTime()

                                }
                            }
                        }
                    }
                }
            }
        }
    }


    // sets the textview to current time +5min
    private fun setCarArrivalTime() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.SECOND, 500)
        binding.timeOfCarArrival.text = DateFormat.getDateTimeInstance().format(calendar.time)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logoutItem -> {
                viewModel.logoutCurrentUser()
                showLogoutDialog(context, navController)
                true
            }
            R.id.historyItem -> {
                navController.navigate(R.id.action_orderFragment_to_historyFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModelFactory = OrderViewModelFactory(googleApiRepository = GoogleApiRepository(),
            authRepository = AuthRepository(fAuth = AppModule.provideAuthRepo(),
                DataBaseRepository(AppModule.provideFireBaseDBRepo())))
        navController = findNavController()
        val store = navController.getViewModelStoreOwner(R.id.nav_graph_order)
        viewModel = ViewModelProvider(store, viewModelFactory)[OrderViewModel::class.java]
        _binding = OrderFragmentBinding.inflate(layoutInflater)
        supportMapFragment = childFragmentManager.findFragmentById(com.example.acar.R.id.map) as SupportMapFragment?
        geoCoder = Geocoder(context)
        viewModel.getAllHistoryRidesFromDb()
        return binding.root
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

    private fun observeAndCreatePolyLines() {
        viewModel.polyLinesLatLng.observe(viewLifecycleOwner) { polyLineLatLngs ->
            val polylineOptions = PolylineOptions()
            supportMapFragment?.getMapAsync { googleMap ->
                if (polyLineLatLngs != null) {
                    for (latLng in polyLineLatLngs) {
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
        if (pickupAddress.isNotEmpty() && destinationAddress.isNotEmpty() && pickupAddress != destinationAddress) {
            viewModel.setPickupAndDestinationAddress(pickupAddress, destinationAddress)
            return true
        }
        else {
            GlobalToast.showShort(context, "Address is empty")
            return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}