package com.kylecorry.trail_sense_maps

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kylecorry.trail_sense_maps.maps.ui.MapsFragment
import com.kylecorry.trailsensecore.infrastructure.system.GeoUriParser
import com.kylecorry.trailsensecore.infrastructure.system.PermissionUtils
import com.kylecorry.trailsensecore.infrastructure.system.doTransaction

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    private var geoIntentLocation: GeoUriParser.NamedCoordinate? = null

    private val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        PermissionUtils.requestPermissions(this, permissions, 1)
    }

    private fun startApp(){
        syncFragmentWithSelection(bottomNavigation.selectedItemId)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            syncFragmentWithSelection(item.itemId)
            true
        }

        val intentData = intent.data
        if (intent.scheme == "geo" && intentData != null) {
            val namedCoordinate = GeoUriParser().parse(intentData)
            geoIntentLocation = namedCoordinate
            bottomNavigation.selectedItemId = R.id.action_maps
        }
    }

    private fun syncFragmentWithSelection(selection: Int){
        when (selection) {
            R.id.action_maps -> {
                val namedCoord = geoIntentLocation
                if (namedCoord != null) {
                    geoIntentLocation = null
                    switchFragment(MapsFragment(namedCoord))
                } else {
                    switchFragment(MapsFragment())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            startApp()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG).show()
        }
    }

    private fun switchFragment(fragment: Fragment){
        supportFragmentManager.doTransaction {
            this.replace(R.id.fragment_holder, fragment)
        }
    }
}