package com.example.acar.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.acar.common.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    private val _isRegistrationSuccessful = authRepository.isRegistrationSuccessful
    val isRegistrationSuccessful : LiveData<Boolean> get() = _isRegistrationSuccessful

    val registrationFailedMessage = authRepository.registrationFailedMessage

    fun register(email: String, password : String){
        authRepository.register(email,password)
    }
    fun doneNavigatingAfterRegistration () {
        authRepository.doneNavigatingAfterRegistration()
    }

}