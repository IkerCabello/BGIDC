package com.idvkm.bgidc.ui.location

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.idvkm.bgidc.databinding.FragmentLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private lateinit var mapView: MapView
    private lateinit var mMap: GoogleMap

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize mapView
        mapView = binding.mapView

        // Configurate the cycle of the mapView
        mapView.onCreate(savedInstanceState)

        // Configurate the callback
        mapView.getMapAsync(this)

        binding.mapsBtn.setOnClickListener {
            val address = "1040,+bul.+Dragan+Tsankov+36,+1113+Sofia"

            // Create the intent
            val gmmIntentUri = Uri.parse("geo:0,0?q=$address")

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            // Verify if Google maps is installed
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(requireContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show()
            }

        }

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val place = LatLng(42.6703098, 23.3508877)
        mMap.addMarker(MarkerOptions().position(place).title("INTERPRED - WTC Sofia"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 16f))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Resumes the MapView
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // Pauses the MapView
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy() // Destroys the MapView to avoid memory leaks
    }
}