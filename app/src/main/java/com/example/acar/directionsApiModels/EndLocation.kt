package com.example.acar.directionsApiModels


import com.google.gson.annotations.SerializedName

data class EndLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)