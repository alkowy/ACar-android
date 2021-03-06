package com.example.acar.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.acar.ordersHistory.RideHistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val fAuth: FirebaseAuth, private val db: DataBaseRepository) {

    private val _currentLoggedInUser = MutableLiveData<FirebaseUser>(fAuth.currentUser)
    val currentLoggedInUser: LiveData<FirebaseUser>
        get() = _currentLoggedInUser

    private val _isRegistrationSuccessful = MutableLiveData<Boolean>()
    val isRegistrationSuccessful: LiveData<Boolean>
        get() = _isRegistrationSuccessful

    private val _registrationFailedMessage = MutableLiveData<String>()
    val registrationFailedMessage: LiveData<String>
        get() = _registrationFailedMessage

    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean>
        get() = _isLoginSuccessful

    private val _loginFailedMessage = MutableLiveData<String>()
    val loginFailedMessage: LiveData<String>
        get() = _loginFailedMessage

    fun doneNavigatingAfterRegistration() {
        _isRegistrationSuccessful.value = false
    }

    fun doneNavigatingAfterLogin() {
        _isLoginSuccessful.value = false
    }

    fun register(email: String, password: String) {
        fAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _currentLoggedInUser.value = fAuth.currentUser
                    GlobalToast.toast = null
                    postUserToDb(User(_currentLoggedInUser.value!!.uid))
                    _isRegistrationSuccessful.value = true
                }
                .addOnFailureListener {
                    _isRegistrationSuccessful.value = false
                    _registrationFailedMessage.value = it.message.toString()
                }
    }

    fun login(email: String, password: String) {
        fAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _currentLoggedInUser.value = fAuth.currentUser
                    _isLoginSuccessful.value = true
                }
                .addOnFailureListener {
                    _isLoginSuccessful.value = false
                    _loginFailedMessage.value = it.message.toString()
                }
    }

    fun logoutCurrentUser() {
        fAuth.signOut()
        _isLoginSuccessful.value = false
    }

    private fun postUserToDb(user: User) {
        val data = hashMapOf("uId" to user.uId)
        db.firebaseDatabase.collection("users").document(_currentLoggedInUser.value?.uid.toString())
                .set(data, SetOptions.merge())
    }
}