package com.example.storyapp.main.maps

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.target.Target
import com.example.storyapp.R
import com.example.storyapp.auth.WelcomeActivity
import com.example.storyapp.data.store.DataStorePreferences
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.utils.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story Map"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        viewModel = ViewModelProvider(
            this, ViewModelFactory(DataStorePreferences.getInstance(dataStore), this)
        )[MapsViewModel::class.java]

        viewModel.getUser().observe(this) { user ->
            if (user.userId.isNotEmpty()) {
                viewModel.getAllStoriesWithLocation("Bearer ${user.token}", 1)
                viewModel.stories.observe(this) { stories ->
                    stories.listStory.forEach { story ->
                        val location = LatLng(story.lat!!, story.lon!!)
                        val markerOptions =
                            MarkerOptions().position(location).snippet(story.description)
                                .title(story.name)
                        val iconSize = 64
                        val imageRequest = ImageRequest.Builder(this)
                            .data(story.photoUrl)
                            .target(object : Target {
                                override fun onStart(placeholder: Drawable?) {
                                    Log.e("MapsActivity", "onStart: ")
                                }

                                override fun onError(error: Drawable?) {
                                    Log.e("MapsActivity", "onError: ")
                                }

                                override fun onSuccess(result: Drawable) {
                                    Log.e("MapsActivity", "onSuccess: ")
                                    val resizedBitmap = Bitmap.createScaledBitmap(
                                        result.toBitmap(),
                                        iconSize,
                                        iconSize,
                                        false
                                    )
                                    markerOptions.icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                            resizedBitmap
                                        )
                                    )
                                    mMap.addMarker(markerOptions)
                                    mMap.moveCamera(
                                        CameraUpdateFactory.newLatLng(
                                            location
                                        )
                                    )
                                }
                            })
                            .build()
                        ImageLoader(this).enqueue(imageRequest)
                    }
                }
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Log.e("MapsActivity", "onCreate: ${viewModel.getUser().value?.userId}")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getMyLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Exception) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}