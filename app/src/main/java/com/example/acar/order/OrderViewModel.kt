package com.example.acar.order

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acar.common.GoogleApiRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Response
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private var googleApiRepository: GoogleApiRepository) : ViewModel() {

    private var _latLngResult = MutableLiveData<LatLng>()
    val latLngResult: LiveData<LatLng> get() = _latLngResult

    private var _stringPickupAddress = MutableLiveData<String>()
    val stringPickupAddress: LiveData<String> get() = _stringPickupAddress

    private var _stringDestinationAddress = MutableLiveData<String>()
    val stringDestinationAddress: LiveData<String> get() = _stringDestinationAddress

    fun setPickupAndDestinationAddress(pickup: String, destination: String) {
        _stringPickupAddress.value = pickup
        _stringDestinationAddress.value = destination
    }

    fun doneMarkingAddress() {
        _stringPickupAddress.value = ""
        _stringDestinationAddress.value = ""
    }

    private var _pickupAndDestinationMarkers = MutableLiveData<MutableList<MarkerOptions>>()
    val pickupAndDestinationMarkers: LiveData<MutableList<MarkerOptions>> get() = _pickupAndDestinationMarkers

    private var _pickupLatLng = MutableLiveData<LatLng>()
    val pickupLatLng: LiveData<LatLng> get() = _pickupLatLng

    private var _destinationLatLng = MutableLiveData<LatLng>()
    val destinationLatLng: LiveData<LatLng> get() = _destinationLatLng

    private var _hasResults = MutableLiveData<Boolean>()
    val hasResults: LiveData<Boolean> get() = _hasResults

    private var _polyLinesLatLng = MutableLiveData<List<LatLng>>()
    val polyLinesLatLng: LiveData<List<LatLng>> get() = _polyLinesLatLng

    private var _routeLength = MutableLiveData<Double>()
    val routeLength: LiveData<Double> get() = _routeLength

    private var _timeOfArrival = MutableLiveData<String>()
    val timeOfArrival: LiveData<String> get() = _timeOfArrival

    private var _estimatedCost = MutableLiveData<Double>()
    val estimatedCost: LiveData<Double> get() = _estimatedCost

    fun clearPickupAndDestinationLatLngs() {
        _pickupLatLng.value = LatLng(0.0, 0.0)
        _destinationLatLng.value = LatLng(0.0, 0.0)
    }


    fun getLatLngFromAddresses(geoCoder: Geocoder) {
        viewModelScope.launch {
            val pickupAddress = async(Dispatchers.IO) {
                geoCoder.getFromLocationName(_stringPickupAddress.value, 5)
            }.await()
            val destinationAddress = async(Dispatchers.IO) {
                geoCoder.getFromLocationName(_stringDestinationAddress.value, 5)
            }.await()
            if (pickupAddress.isNotEmpty() && destinationAddress.isNotEmpty()) {
                _hasResults.value = true
                val pickupLocation = pickupAddress[0]
                val destinationLocation = destinationAddress[0]
                _pickupLatLng.value = LatLng(pickupLocation.latitude, pickupLocation.longitude)
                _destinationLatLng.value = LatLng(destinationLocation.latitude, destinationLocation.longitude)
            }
            else {
                _hasResults.value = false
            }
        }

    }

    fun generatePickupAndDestinationMarkers() {
        val markers = mutableListOf<MarkerOptions>()
        val pickupMarker = MarkerOptions().position(_pickupLatLng.value!!)
        val destinationMarker = MarkerOptions().position(_destinationLatLng.value!!)
        markers.add(pickupMarker)
        markers.add(destinationMarker)
        _pickupAndDestinationMarkers.value = markers
    }

    // gets duration in seconds and adds it to the current time -> formats the Calendar into String and sets _timeOfArrival
    fun calculateTimeOfArrival() {
        viewModelScope.launch {
            val duration =
                async { googleApiRepository.getDuration(stringPickupAddress.value!!, stringDestinationAddress.value!!) }.await()
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            Log.d("OrderViewModel", "duration in calculateTimeOfArrival" + duration.toString())
            calendar.add(Calendar.SECOND, duration.toInt())
            _timeOfArrival.value = DateFormat.getDateTimeInstance().format(calendar.time)
        }
    }


    fun getPolylineLatLngs() {
        viewModelScope.launch {
            val polyLinesLatLngs = async {
                googleApiRepository.getPolyLines(stringPickupAddress.value!!, stringDestinationAddress.value!!)
            }.await()
            _polyLinesLatLng.postValue(polyLinesLatLngs)
        }

    }

    fun doneShowingNoResultsToast() {
        _hasResults.postValue(true)
    }

    fun calculateRouteLengthAndCost() {
        viewModelScope.launch {
            val routeLength =
                async { googleApiRepository.getRouteLength(stringPickupAddress.value!!, stringDestinationAddress.value!!) }.await()
            _routeLength.postValue(routeLength / 1000)
            _estimatedCost.postValue(routeLength.div(1000).times(3))
        }
    }

}

