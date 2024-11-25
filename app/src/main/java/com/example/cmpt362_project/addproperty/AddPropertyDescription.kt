package com.example.cmpt362_project.addproperty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.cmpt362_project.R
import com.example.cmpt362_project.databinding.AddPropertyDescriptionBinding

class AddPropertyDescription : AppCompatActivity() {

    private lateinit var binding : AddPropertyDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddPropertyDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.DescriptionToolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        // set up the tool bar
        setSupportActionBar(binding.DescriptionToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.DescriptionToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }
}