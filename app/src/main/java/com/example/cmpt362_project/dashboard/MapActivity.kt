package com.example.cmpt362_project.dashboard

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.cmpt362_project.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Parcelize
data class LocationData(val latitude: Double, val longitude: Double, val title: String) : Parcelable

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)

        // 接收从 DashboardFragment 传递的邮政编码列表
        val postalCodes = intent.getStringArrayListExtra("postal_codes") ?: emptyList()

        // 设置工具栏
        val toolbar: Toolbar = findViewById(R.id.mapToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Map"
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            // 更新地图
            updateMapWithPostalCodes(postalCodes)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun updateMapWithPostalCodes(postalCodes: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val locations = postalCodes.mapNotNull { postalCode ->
                fetchLatLngFromPostalCode(postalCode)
            }

            withContext(Dispatchers.Main) {
                // 在地图上添加标记
                locations.forEach { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(latLng).title(location.title))
                }

                // 将相机移动到第一个标记的位置
                if (locations.isNotEmpty()) {
                    val firstLocation = locations.first()
                    val latLng = LatLng(firstLocation.latitude, firstLocation.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                } else {
                    // 如果没有找到位置，显示提示信息
                    showToast("No locations found for the provided postal codes.")
                }
            }
        }
    }

    private fun fetchLatLngFromPostalCode(postalCode: String): LocationData? {
        val apiKey = getApiKey()
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=${postalCode.replace(" ", "+")}&key=$apiKey"

        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            val results = jsonResponse.getJSONArray("results")
            if (results.length() > 0) {
                val location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                val latitude = location.getDouble("lat")
                val longitude = location.getDouble("lng")
                LocationData(latitude, longitude, postalCode)
            } else {
                null // 没有找到结果
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // 发生错误
        }
    }

    private fun getApiKey(): String {
        return try {
            val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            ai.metaData.getString("com.google.android.geo.API_KEY") ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
