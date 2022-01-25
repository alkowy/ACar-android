package com.example.acar.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.acar.common.AuthRepository
import com.example.acar.common.GoogleApiRepository

class OrderViewModelFactory constructor(private val googleApiRepository: GoogleApiRepository, private val authRepository: AuthRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            OrderViewModel(this.googleApiRepository,this.authRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }    }
}