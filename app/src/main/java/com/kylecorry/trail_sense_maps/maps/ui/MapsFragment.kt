package com.kylecorry.trail_sense_maps.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kylecorry.trail_sense_maps.R
import com.kylecorry.trail_sense_maps.maps.domain.Coordinate
import com.kylecorry.trail_sense_maps.maps.infrastructure.GeoUriParser
import com.kylecorry.trail_sense_maps.sensors.DeclinationCalculator
import com.kylecorry.trail_sense_maps.sensors.GPS
import com.kylecorry.trail_sense_maps.sensors.SensorChecker
import com.kylecorry.trail_sense_maps.sensors.VectorCompass
import com.kylecorry.trail_sense_maps.system.UiUtils

class MapsFragment(private val initialDestination: GeoUriParser.NamedCoordinate? = null) :
    Fragment() {

    constructor() : this(null)

    private lateinit var mapView: CustomMapView
    private lateinit var compassView: ImageView
    private lateinit var locationLockBtn: FloatingActionButton

    private var destination: Coordinate? = initialDestination?.coordinate

    private val gps by lazy { GPS(requireContext()) }
    private val compass by lazy { VectorCompass(requireContext()) }
    private val sensorChecker by lazy { SensorChecker(requireContext()) }
    private val declinationCalculator = DeclinationCalculator()

    private var wasCentered = false
    private var rotateMap = false
    private var keepCentered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CustomMapView.configure(context)
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        if (!sensorChecker.hasGPS()) {
            // TODO: Can't do anything
        }

        compassView = view.findViewById(R.id.map_compass)
        mapView = CustomMapView(view.findViewById(R.id.map), compassView, gps.location)
        locationLockBtn = view.findViewById(R.id.location_lock_btn)

        UiUtils.setButtonState(locationLockBtn, keepCentered)

        compassView.setOnClickListener {
            rotateMap = !rotateMap
        }

        locationLockBtn.setOnClickListener {
            keepCentered = !keepCentered
            if (keepCentered){
                mapView.showLocation(gps.location)
            }
            UiUtils.setButtonState(locationLockBtn, keepCentered)
        }

        mapView.showLocation(gps.location)
        mapView.setMyLocation(gps.location)

        return view
    }


    override fun onResume() {
        super.onResume()
        CustomMapView.configure(context)
        mapView.onResume()
        gps.start(this::onLocationUpdate)
        compass.start(this::onCompassUpdate)

        val d = destination
        if (d != null) {
            mapView.showBeacon(d)
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        gps.stop(this::onLocationUpdate)
        compass.stop(this::onCompassUpdate)
    }

    private fun update() {
        mapView.setMyLocation(gps.location)
        mapView.setCompassAzimuth(compass.bearing.value)

        if (rotateMap) {
            mapView.setMapAzimuth(compass.bearing.value)
            mapView.setMyLocationAzimuth(0f)
        } else {
            mapView.setMapAzimuth(0f)
            mapView.setMyLocationAzimuth(compass.bearing.value)
        }

        val d = destination
        if (d != null && gps.location.distanceTo(d) < 10) {
            destination = null
            mapView.hideBeacon()
            UiUtils.longToast(requireContext(), getString(R.string.arrived))
        }

        if (keepCentered){
            mapView.showLocation(gps.location)
        }

    }

    private fun onLocationUpdate(): Boolean {
        if (!wasCentered) {
            mapView.showLocation(gps.location)
            wasCentered = true
        }

        compass.declination = declinationCalculator.calculate(gps.location, gps.altitude)

        update()
        return true
    }

    private fun onCompassUpdate(): Boolean {
        update()
        return true
    }

}
