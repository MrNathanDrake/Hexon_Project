package com.example.cmpt362_project.authority

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

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

    // 注册方法
    fun signUp(email: String, password: String, confirmPassword: String) {
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
                    _authStatus.value = AuthStatus.Success
                } else {
                    _authStatus.value =
                        AuthStatus.Error(task.exception?.message ?: "Registration Failed")
                }
            }
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
    object Idle : AuthStatus()                // 初始状态
    object Success : AuthStatus()             // 成功状态
    data class Error(val message: String) : AuthStatus()  // 错误状态
}
