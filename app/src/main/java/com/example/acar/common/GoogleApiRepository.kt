package com.example.acar.common


import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.acar.ACarApplication
import com.example.acar.BuildConfig

import com.example.acar.R
import com.example.acar.directionsApiModels.DirectionsModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


@Singleton
class GoogleApiRepository @Inject constructor() {

    private val apiKey = BuildConfig.MAPS_API_KEY

    // returns a retrofit response - DirectionsModel
    suspend fun getDirectionsResponse(origin: String, destination: String): Response<DirectionsModel> {
        var response: Response<DirectionsModel>
        val directions =
            Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build()
                .create(DirectionsRequests::class.java)
        withContext(Dispatchers.IO) {
            response = directions.getDirections(origin, destination, apiKey).awaitResponse()
        }
        Log.d("googleapirepository","getdirectionsresponse       " + response.toString())
        return response
    }
}

