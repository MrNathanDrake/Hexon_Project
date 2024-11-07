package com.example.cmpt362_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cmpt362_project.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setContentView(binding.root)

        // 加载并显示保存的数据
        loadProfileData()

        // 设置保存按钮点击事件
        binding.saveButton.setOnClickListener {
            val firstName = binding.firstNameEdit.text.toString()
            val lastName = binding.lastNameEdit.text.toString()
            val email = binding.emailEdit.text.toString()

            // 保存数据到 SharedPreferences
            saveProfileData(firstName, lastName, email)
            Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileData(firstName: String, lastName: String, email: String) {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("FIRST_NAME", firstName)
            putString("LAST_NAME", lastName)
            putString("EMAIL", email)
            apply()
        }
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        binding.firstNameEdit.setText(sharedPref.getString("FIRST_NAME", ""))
        binding.lastNameEdit.setText(sharedPref.getString("LAST_NAME", ""))
        binding.emailEdit.setText(sharedPref.getString("EMAIL", ""))
    }
}