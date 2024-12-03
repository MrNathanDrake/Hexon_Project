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

        // Observe registration status
        authViewModel.authStatus.observe(this, Observer { status ->
            when (status) {
                is AuthStatus.Success -> navigateToSignIn()
                is AuthStatus.Error -> Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        })

        // Sign in click
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val name = binding.nameET.text.toString()
            val email = binding.emailEt.text.toString().trim();
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            authViewModel.signUp(name, email, pass, confirmPass)
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}