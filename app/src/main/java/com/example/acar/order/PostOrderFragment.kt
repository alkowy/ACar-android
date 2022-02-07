package com.example.acar.order

import android.R.attr
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.acar.R
import com.example.acar.common.GoogleApiRepository
import com.example.acar.databinding.FragmentPostOrderBinding
import com.example.acar.databinding.OrderFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import android.R.attr.delay
import android.app.AlertDialog
import android.os.Build
import android.text.format.DateFormat
import android.text.format.Time
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.ViewUtils
import com.example.acar.common.AppModule
import com.example.acar.common.AuthRepository
import com.example.acar.common.DataBaseRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime


@AndroidEntryPoint
class PostOrderFragment : Fragment() {

    private lateinit var viewModel: OrderViewModel
    private var _binding: FragmentPostOrderBinding? = null
    private val binding get() = _binding!!
    private var supportMapFragment: SupportMapFragment? = null
    private lateinit var geoCoder: Geocoder
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModelFactory = OrderViewModelFactory(googleApiRepository = GoogleApiRepository(),
            authRepository = AuthRepository(fAuth = AppModule.provideAuthRepo(),
                DataBaseRepository(AppModule.provideFireBaseDBRepo())))
        navController = findNavController()
        val store = navController.getViewModelStoreOwner(R.id.nav_graph_order)
        viewModel = ViewModelProvider(store, viewModelFactory)[OrderViewModel::class.java]
        _binding = FragmentPostOrderBinding.inflate(layoutInflater)
        supportMapFragment = childFragmentManager.findFragmentById(com.example.acar.R.id.PostOrderMap) as SupportMapFragment?
        geoCoder = Geocoder(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calculateTimeOfArrivalAndChangeText()
        calculateRouteLengthAndCostAndChangeText()
        getAndDrawPolylinesAndMarkers()

        hereBtnOnClick()
    }

    private fun hereBtnOnClick() {
        binding.hereBtn.setOnClickListener {
            displayThankYouAlertDialogAndNavigateBackToOrderFragment()
            viewModel.addRideToHistory()
        }
    }

    private fun displayThankYouAlertDialogAndNavigateBackToOrderFragment() {
        AlertDialog.Builder(context)
            .setTitle("Ride finished")
            .setMessage("Thank you for using our service! :)")
            .setIcon(android.R.drawable.sym_def_app_icon)
            .setOnCancelListener { navController.navigate(R.id.action_postOrderFragment_to_orderFragment) }
            .show()
    }


    private fun calculateRouteLengthAndCostAndChangeText() {
        viewModel.calculateRouteLengthAndCost()
        viewModel.routeLength.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.estRouteLength.text = "Route length: ${String.format("%.1f", it)}km"
            }
        }
        viewModel.estimatedCost.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.costTxt.text = "Cost: ${String.format("%.2f", it)}zł"
            }
        }
    }

    private fun calculateTimeOfArrivalAndChangeText() {
        viewModel.calculateTimeOfArrival()
        viewModel.timeOfArrival.observe(viewLifecycleOwner) {
            if (it != null) {
//                binding.estDestinationArrivalTxt.text = "Estimated time of arrival: $it"
                binding.estDestinationArrivalTxt.text = getString(R.string.estTimeOfArrival,it)
            }
        }
    }

    private fun getAndDrawPolylinesAndMarkers() {
        // get and draw polylines
        viewModel.getPolylineLatLngs()
        var position: LatLng
        val latLngBuilder = LatLngBounds.Builder()
        var bounds: LatLngBounds?
        viewModel.polyLinesLatLng.observe(viewLifecycleOwner) { polyLineLatLngs ->
            val polylineOptions = PolylineOptions()
            supportMapFragment?.getMapAsync { googleMap ->
                googleMap.clear()
                if (polyLineLatLngs != null) {
                    for (latLng in polyLineLatLngs) {
                        polylineOptions.add(latLng)
                    }
                }
                googleMap.addPolyline(polylineOptions.width(5F).color(Color.BLUE))
                //create destination and pickup markers
                viewModel.pickupAndDestinationMarkers.observe(viewLifecycleOwner) { markerOptions ->
                    if (markerOptions.isNotEmpty()) {
                        markerOptions.forEach { marker ->
                            position = marker.position
                            latLngBuilder.include(LatLng(position.latitude, position.longitude))
                            googleMap.addMarker(marker)
                        }
                        //bounds = latLngBuilder.include(LatLng(viewModel.pickupLatLng.value!!.latitude, viewModel.pickupLatLng.value!!.longitude)).build()  <- animate to pickup location instead of a whole route
                        bounds = latLngBuilder.build()
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds!!, 200))
                    }

                }

            }
        }

    }
}
