package com.example.cmpt362_project.dashboard

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
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
data class LocationData(val latitude: Double, val longtitude: Double, val title: String) :
    Parcelable

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    // List of postal codes to be resolved
    private val postalCodes = listOf("V6T 1Z4", "V5A 1S6", "V3T 0A3", "V6B 5K3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Fetch locations for postal codes and update map
        CoroutineScope(Dispatchers.IO).launch {
            val locations = postalCodes.mapNotNull { postalCode ->
                fetchLatLngFromPostalCode(postalCode)
            }

            withContext(Dispatchers.Main) {
                // Add markers to the map
                locations.forEach { location ->
                    val latLng = LatLng(location.latitude, location.longtitude)
                    map.addMarker(MarkerOptions().position(latLng).title(location.title))
                }

                // Move camera to the first location if available
                if (locations.isNotEmpty()) {
                    val firstLocation = locations.first()
                    val latLng = LatLng(firstLocation.latitude, firstLocation.longtitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }
            }
        }
    }

    private fun fetchLatLngFromPostalCode(postalCode: String): LocationData? {
        val apiKey = getApiKey() // Dynamically get the API Key
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
                null // No results found for the postal code
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Error occurred
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
}
