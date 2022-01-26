package com.example.acar.common

import android.util.Log
import com.example.acar.ordersHistory.RideHistoryItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataBaseRepository @Inject constructor(val firebaseDatabase: FirebaseFirestore) {

    fun addRideToTheHistoryDb(userId: String, ride: RideHistoryItem) {
        firebaseDatabase.collection("users").document(userId).collection("ridesHistory").add(ride)
    }

    suspend fun getRidesHistory(userId: String): ArrayList<RideHistoryItem> {
        return withContext(Dispatchers.IO) {
            val historyOfRides = arrayListOf<RideHistoryItem>()
            val docRef = firebaseDatabase.collection("users").document(userId).collection("ridesHistory")
            val documents = docRef.get().await()
            documents.let { ride ->
                ride.forEach {
                    val date = it.get("date").toString()
                    val destination = it.get("destination").toString()
                    val pickup = it.get("pickup").toString()
                    val polylineOverview = it.get("polyLineOverview").toString()
                    val destinationLatLngHashMap = it.get("destinationLatLng") as HashMap<String, String>
                    val destinationLatitude = destinationLatLngHashMap["latitude"].toString().toDouble()
                    val destinationLongitude = destinationLatLngHashMap["longitude"].toString().toDouble()
                    val destinationLatLng = com.google.android.gms.maps.model.LatLng(destinationLatitude, destinationLongitude)

                    val pickupLatLngHashMap = it.get("pickupLatLng") as HashMap<String, String>
                    val pickupLatitude = pickupLatLngHashMap["latitude"].toString().toDouble()
                    val pickupLongitude = pickupLatLngHashMap["longitude"].toString().toDouble()
                    val pickupLatLng = com.google.android.gms.maps.model.LatLng(pickupLatitude, pickupLongitude)
                    val ride = RideHistoryItem(date, pickup, destination, polylineOverview, pickupLatLng, destinationLatLng)
                    Log.d("DatabaseRepository ride", ride.toString())
                    historyOfRides.add(ride)
                }
            }
            Log.d("DatabaseRepository rides", historyOfRides.toString())
            historyOfRides
        }
    }
}