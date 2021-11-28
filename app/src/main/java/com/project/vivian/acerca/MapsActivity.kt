package com.project.vivian.acerca

import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.vivian.R
import com.google.android.gms.maps.MapView
import com.project.vivian.menu.MenuActivity
import kotlinx.android.synthetic.main.activity_maps.*
import android.content.Intent





class MapsActivity: AppCompatActivity(), OnMapReadyCallback  {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            map = googleMap

            val positionMarket = LatLng(-12.101407, -77.029791)
            map.addMarker(
                MarkerOptions()
                    .position(positionMarket)
                    .title("Vivian Sede Principal")
            )

            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(positionMarket, 14f),
                2000,
                null
            )

            map.uiSettings.isZoomControlsEnabled = true
        }
    }
}