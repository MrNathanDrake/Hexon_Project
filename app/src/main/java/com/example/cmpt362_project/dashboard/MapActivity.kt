package com.example.cmpt362_project.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cmpt362_project.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.Serializable

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val locations = intent.getSerializableExtra("locations") as List<LocationData>

        // add markers for each location
        locations?.forEach { location ->
            val latlng = LatLng(location.latitude, location.longtitude)
            map.addMarker(MarkerOptions().position(latlng).title(location.title))
        }

        // move camera to the first location if available
        if (locations.isNullOrEmpty().not()) {
            val firstLocation = locations!!.first()
            val latLng = LatLng(firstLocation.latitude, firstLocation.longtitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
        }
    }
}

data class LocationData(val latitude: Double, val longtitude: Double, val title: String) : Serializable

