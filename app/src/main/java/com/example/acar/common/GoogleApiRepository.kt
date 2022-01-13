package com.example.acar.common


import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.acar.ACarApplication
import com.example.acar.BuildConfig

import com.example.acar.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


@Singleton
class GoogleApiRepository @Inject constructor() {

    private val apiKey = BuildConfig.MAPS_API_KEY

    private var _directionsResponse = MutableLiveData<ResponseBody>()
    val directionsResponse: LiveData<ResponseBody> get() = _directionsResponse

    fun setDirectionsResponse(response: ResponseBody) {
        _directionsResponse.value = response
    }

    // returns duration in seconds
    suspend fun getDuration(origin: String, destination: String): Long {
        var duration: Long = 0
        val directions =
            Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build()
                .create(DirectionsRequests::class.java)

        withContext(Dispatchers.IO) {
            val response = directions.getDirections(origin, destination, apiKey).awaitResponse()
            Log.d("googleapirepo", response.toString())
            if (response.isSuccessful) {
                Log.d("googleapirepository", response.body().toString())
                val routes = response.body()?.routes
                Log.d("googleapirepository",
                    "getduration overviewpolyline           " + routes!!.first().overviewPolyline.points.toString())
                for (route in routes) {
                    for (leg in route.legs) {
                        duration += leg.duration.value
                    }
                }
            }
        }
        Log.d("googleapirepository", "getduration nizej        " + duration)
        return duration
    }

    // returns list of LatLngs to create polylines on the map
    suspend fun getPolyLines(origin: String, destination: String): List<LatLng> {
        Log.d("googleapirepo", "   getPolyLinesFun")

        val polyLines = mutableListOf<LatLng>()
        val directions =
            Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build()
                .create(DirectionsRequests::class.java)

        withContext(Dispatchers.IO) {
            val response = directions.getDirections(origin, destination, apiKey).awaitResponse()
            Log.d("googleapirepo", response.toString())
            if (response.isSuccessful) {
                Log.d("googleapirepository", response.body().toString())
                val routes = response.body()?.routes
                if (routes != null) {
                    Log.d("googleapirepository", "overview polyline           " + routes!!.first().overviewPolyline.points.toString())
                    for (route in routes) {
                        val tempLatLng = PolyUtil.decode(route.overviewPolyline.points)
                        polyLines.addAll(tempLatLng)
                    }
                }
            }
        }
        Log.d("googleapirepository", "getpolylines        " + polyLines)
        return polyLines
    }

    // returns route length in meters
    suspend fun getRouteLength(origin: String, destination: String): Double {
        var routeLength = 0
        val directions =
            Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build()
                .create(DirectionsRequests::class.java)
        withContext(Dispatchers.IO) {
            val response = directions.getDirections(origin, destination, apiKey).awaitResponse()
            if (response.isSuccessful) {
                val routes = response.body()?.routes
                for (route in routes!!) {
                    for (leg in route.legs) {
                        routeLength += leg.distance.value
                    }
                }
            }
        }
        return routeLength.toDouble()
    }
}

