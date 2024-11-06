package com.example.cmpt362_project.authority

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cmpt362_project.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 观察注册状态
        authViewModel.authStatus.observe(this, Observer { status ->
            when (status) {
                is AuthStatus.Success -> navigateToSignIn()
                is AuthStatus.Error -> Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        })

        // sign in click
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            authViewModel.signUp(email, pass, confirmPass)
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}