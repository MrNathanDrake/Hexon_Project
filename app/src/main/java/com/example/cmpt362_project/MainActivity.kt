package com.example.cmpt362_project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.cmpt362_project.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

//    // 注册权限请求
//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // 用户已授予权限
//            Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
//        } else {
//            // 用户拒绝权限
//            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_addproperty, R.id.navigation_inbox, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)

        // 检查并请求位置权限
//        checkAndRequestLocationPermission()
    }

//    private fun checkAndRequestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // 请求位置权限
//            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        } else {
//            // 权限已授予
//            Toast.makeText(this, "Location permission already granted", Toast.LENGTH_SHORT).show()
//        }
//    }
}
