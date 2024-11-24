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

    // 登录和注册状态的 LiveData
    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> get() = _authStatus

    // 登录方法
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

    // sign up function
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

    // 检查是否已登录
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // 清除状态
    fun clearStatus() {
        _authStatus.value = AuthStatus.Idle
    }

}

// 认证状态封装类
sealed class AuthStatus {
    object Idle : AuthStatus()                // 初始状态 (initial or neutral state)
    object Success : AuthStatus()             // 成功状态 (Successful operation state)
    data class Error(val message: String) : AuthStatus()  // 错误状态 (Error state with a message)
}
