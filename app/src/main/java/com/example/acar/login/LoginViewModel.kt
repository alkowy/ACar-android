package com.example.acar.login

import androidx.lifecycle.ViewModel
import com.example.acar.common.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private var authRepository: AuthRepository) : ViewModel() {

    var isLoginSuccessful = authRepository.isLoginSuccessful

    val failedLoginMessage = authRepository.loginFailedMessage

    fun login(email: String, password: String) {
        authRepository.login(email, password)
        isLoginSuccessful = authRepository.isLoginSuccessful
    }

    fun doneNavigatingAfterSuccessfulLogin() {
        authRepository.doneNavigatingAfterLogin()
    }

}