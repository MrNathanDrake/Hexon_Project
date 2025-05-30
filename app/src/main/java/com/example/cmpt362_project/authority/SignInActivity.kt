package com.example.cmpt362_project.authority

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cmpt362_project.MainActivity
import com.example.cmpt362_project.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

         // Check if the user is logged in
        if (authViewModel.isUserLoggedIn()) {
            navigateToMain()
        }

        // Observe registration status
        authViewModel.authStatus.observe(this, Observer { status ->
            when (status) {
                is AuthStatus.Success -> navigateToMain()
                is AuthStatus.Error -> Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        })

        // Sign-in button click
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Set up sign in button, retrieves the input and pass it to the authViewModel
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString()
            authViewModel.signIn(email, pass)
        }
    }

    // Navigates to main activity
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}