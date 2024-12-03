package com.example.cmpt362_project.authority

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cmpt362_project.inbox.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mDbRef: DatabaseReference

    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> get() = _authStatus

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authStatus.value = AuthStatus.Error("Empty Fields Are Not Allowed!")
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authStatus.value = AuthStatus.Success
                } else {
                    _authStatus.value = AuthStatus.Error(task.exception?.message ?: "Login Failed")
                }
            }
    }

    // Sign up function
    fun signUp(name: String ,email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authStatus.value = AuthStatus.Error("Empty Fields Are Not Allowed!")
            return
        }

        if (password != confirmPassword) {
            _authStatus.value = AuthStatus.Error("Passwords do not match")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,firebaseAuth.currentUser?.uid!! )
                    _authStatus.value = AuthStatus.Success
                } else {
                    _authStatus.value =
                        AuthStatus.Error(task.exception?.message ?: "Registration Failed")
                }
            }
    }

    private fun addUserToDatabase(name: String,email: String,uid:String){
        mDbRef= FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(user(name,email,uid))

    }

    // Check if you are logged in
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun clearStatus() {
        _authStatus.value = AuthStatus.Idle
    }

}

// Authentication Status Class
sealed class AuthStatus {
    object Idle : AuthStatus()                // Initial state
    object Success : AuthStatus()             // Successful operation state
    data class Error(val message: String) : AuthStatus()  // Error state with a message
}
