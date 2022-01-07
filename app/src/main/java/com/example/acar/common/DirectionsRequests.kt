package com.example.acar.common

import com.example.acar.directionsApiModels.DirectionsModel
import com.example.acar.directionsApiModels.Duration
import com.example.acar.directionsApiModels.Leg
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsRequests {

    @GET("maps/api/directions/json?")
    fun getDirections(@Query("origin") origin: String, @Query("destination") destination: String,
                      @Query("key") key: String): Call<DirectionsModel>

}