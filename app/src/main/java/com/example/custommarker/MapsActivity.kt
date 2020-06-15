package com.example.custommarker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import com.example.custommarker.marker.MapMarker

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val rootViewGroup: ViewGroup by lazy { findViewById<ViewGroup>(android.R.id.content) }

    private var customMarkerList: MutableList<MapMarker> = mutableListOf()
    private var markerMap: MutableMap<Marker, MapMarker> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setExpandedMarker()
        setDropMarker()
        setETAMarker()
        setMarkerClickListener()
    }

    private fun setMarkerClickListener() {
        mMap.setOnMarkerClickListener {
            when (it.tag) {
                "ETA" -> {
                    Toast.makeText(this, "ETA", Toast.LENGTH_SHORT).show()
                }
                "DROP" -> {
                    Toast.makeText(this, "DROP", Toast.LENGTH_SHORT).show()
                }
                "EXPAND" -> {
                    Toast.makeText(this, "EXPAND", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "ELSE", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

    }

    private fun setExpandedMarker() {
        val destinationMarker = MapMarker(
            rootViewGroup,
            MapMarker.Type.Destination("Expand", "Marker in Expanded"),
            MapMarker.State.Expanded
        )

        val sydney = LatLng(-33.865143, 151.209900)
        val options = MarkerOptions()
            .position(sydney)
            .icon(destinationMarker.getGoogleMapsMarker())
        val marker = mMap.addMarker(options)
        marker.tag = "EXPAND"
        markerMap[marker] = destinationMarker
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        customMarkerList.add(destinationMarker)
    }

    private fun setDropMarker() {
        val destinationMarker = MapMarker(
            rootViewGroup,
            MapMarker.Type.Destination(),
            MapMarker.State.Dropped
        )

        val sydney = LatLng(-34.921230, 138.599503)
        val options = MarkerOptions()
            .position(sydney)
            .icon(destinationMarker.getGoogleMapsMarker())
        val marker = mMap.addMarker(options)
        marker.tag = "DROP"
        markerMap[marker] = destinationMarker
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        customMarkerList.add(destinationMarker)
    }

    private fun setETAMarker() {
        val destinationMarker = MapMarker(
            rootViewGroup,
            MapMarker.Type.DestinationLeft("23 Min"),
            MapMarker.State.ExpandedLeft
        )

        val sydney = LatLng(-20.917574, 142.702789)
        val options = MarkerOptions()
            .position(sydney)
            .icon(destinationMarker.getGoogleMapsMarker())
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        val marker = mMap.addMarker(options)
        marker.tag = "ETA"
        markerMap[marker] = destinationMarker
        customMarkerList.add(destinationMarker)
    }

    override fun onDestroy() {
        super.onDestroy()
        customMarkerList.forEach { it.remove() }
        markerMap.clear()
        customMarkerList.clear()
    }
}
