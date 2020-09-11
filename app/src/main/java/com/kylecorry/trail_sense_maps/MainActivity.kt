package com.kylecorry.trail_sense_maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kylecorry.trail_sense_maps.maps.infrastructure.GeoUriParser
import com.kylecorry.trail_sense_maps.maps.ui.MapsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    private var geoIntentLocation: GeoUriParser.NamedCoordinate? = null

    private val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        if (!hasPermissions()){
            getPermission()
        } else {
            startApp()
        }
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

    private fun hasPermissions(): Boolean {
        for (permission in permissions){
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }

        return true
    }

    private fun getPermission(){
        ActivityCompat.requestPermissions(this,
            permissions.toTypedArray(),
            1
        )
    }

    private fun switchFragment(fragment: Fragment){
        supportFragmentManager.doTransaction {
            this.replace(R.id.fragment_holder, fragment)
        }
    }
}