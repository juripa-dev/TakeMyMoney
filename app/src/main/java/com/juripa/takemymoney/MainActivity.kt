package com.juripa.takemymoney

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.juripa.takemymoney.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOCATION_PERMISSION_CODE = 1000
    }

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initMap()
        initViewModel()

        viewModel.onInit()
    }

    private fun initMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_layout) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            viewModel.onInitMap()
        }
    }

    private fun initViewModel() {
        viewModel.showToast.asLiveData().observe(this) { showToast(it) }

        viewModel.showProgress.asLiveData().observe(this) { showProgress() }

        viewModel.hideProgress.asLiveData().observe(this) { hideProgress() }

        viewModel.loadUrl.asLiveData().observe(this) { startWebView(it) }

        viewModel.requestLocation.asLiveData().observe(this) { requestLocation(it) }

        viewModel.pointMarker.asLiveData().observe(this) { pointMark(it) }

        viewModel.releaseRefresh.asLiveData().observe(this) { binding.swipeLayout.isRefreshing = false }

        viewModel.findLocation.asLiveData().observe(this) { findTargetLocation() }
    }

    private fun pointMark(latLng: LatLng) {
        val option = MarkerOptions().apply {
            position(latLng)
        }
        googleMap?.addMarker(option)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }

    private fun showProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
    }

    private fun startWebView(url: String) {
        startActivity(
            Intent(this, WebViewActivity::class.java).apply {
                putExtra("WEB_URL", url)
            }
        )
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    @SuppressWarnings("MissingPermission")
    private fun requestLocation(onSuccess: (Double, Double) -> Unit) {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            fastestInterval = 1000
        }

        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let {
                    for((_, location) in it.locations.withIndex()) {
                        onSuccess.invoke(location.latitude, location.longitude)
                        locationClient.removeLocationUpdates(this)
                    }
                }
            }
        }

        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
    }

    @SuppressWarnings("MissingPermission")
    private fun findTargetLocation() {
        checkLocationPermission {
            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    requestLocation { lat, long ->
                        viewModel.setTargetLocation(lat, long)
                    }
                } else {
                    viewModel.setTargetLocation(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun checkLocationPermission(action: () -> Unit) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            action.invoke()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findTargetLocation()
                }
            }
        }
    }
}