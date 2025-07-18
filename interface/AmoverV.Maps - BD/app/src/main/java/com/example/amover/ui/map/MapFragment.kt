package com.example.amover.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.amover.R
import com.example.amover.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment() : Fragment(), OnMapReadyCallback {

    //

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
//
        return root
    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        val local = LatLng(41.28866109528603, -7.738083850417262) // Podes trocar pelas coordenadas que quiseres
        mMap.addMarker(MarkerOptions().position(local).title("UTAD"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 15f))


    }


}