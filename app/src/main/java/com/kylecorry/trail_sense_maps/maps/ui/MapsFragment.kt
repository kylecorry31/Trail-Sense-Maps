package com.kylecorry.trail_sense_maps.maps.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kylecorry.trail_sense_maps.R
import com.kylecorry.trail_sense_maps.databinding.FragmentMapsBinding
import com.kylecorry.trail_sense_maps.maps.infrastructure.ShareService
import com.kylecorry.trail_sense_maps.maps.infrastructure.TrailSense
import com.kylecorry.trailsensecore.domain.geo.Coordinate
import com.kylecorry.trailsensecore.domain.geo.GeoService
import com.kylecorry.trailsensecore.domain.units.Distance
import com.kylecorry.trailsensecore.domain.units.DistanceUnits
import com.kylecorry.trailsensecore.infrastructure.sensors.SensorChecker
import com.kylecorry.trailsensecore.infrastructure.sensors.compass.VectorCompass
import com.kylecorry.trailsensecore.infrastructure.sensors.declination.DeclinationProvider
import com.kylecorry.trailsensecore.infrastructure.sensors.gps.GPS
import com.kylecorry.trailsensecore.infrastructure.system.GeoUriParser
import com.kylecorry.trailsensecore.infrastructure.system.IntentUtils
import com.kylecorry.trailsensecore.infrastructure.system.UiUtils
import com.kylecorry.trailsensecore.infrastructure.time.Throttle
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.views.MapView
import kotlin.math.roundToInt

class MapsFragment(private val initialDestination: GeoUriParser.NamedCoordinate? = null) :
    Fragment() {

    constructor() : this(null)

    private lateinit var mapView: CustomMapView
    private lateinit var cacheManager: CacheManager

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var destination: Coordinate? = initialDestination?.coordinate

    private val gps by lazy { GPS(requireContext()) }
    private val compass by lazy { VectorCompass(requireContext(), 32, true) }
    private val sensorChecker by lazy { SensorChecker(requireContext()) }
    private val geoService = GeoService()

    private val throttle = Throttle(20)

    private var wasCentered = false
    private var rotateMap = false
    private var keepCentered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        CustomMapView.configure(context)

        if (!sensorChecker.hasGPS()) {
            // TODO: Can't do anything
        }

        mapView = CustomMapView(binding.map, binding.mapCompass, gps.location)
        cacheManager = CacheManager(binding.map)

        UiUtils.setButtonState(binding.locationLockBtn, keepCentered, UiUtils.color(requireContext(), R.color.colorPrimary), UiUtils.color(requireContext(), R.color.colorSecondary))

        binding.mapCompass.setOnClickListener {
            rotateMap = !rotateMap
        }

        binding.locationLockBtn.setOnClickListener {
            keepCentered = !keepCentered
            if (keepCentered){
                mapView.showLocation(gps.location)
            }
            UiUtils.setButtonState(binding.locationLockBtn, keepCentered, UiUtils.color(requireContext(), R.color.colorPrimary), UiUtils.color(requireContext(), R.color.colorSecondary))
        }

        mapView.showLocation(gps.location)
        mapView.setMyLocation(gps.location)
        mapView.onLocationSelected = { location ->
            // TODO: Prompt user to either navigate there or share location
            destination = location
            mapView.showBeacon(location)
            ShareService.shareLocation(requireContext(), location)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        if (throttle.isThrottled()){
            return
        }

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
            binding.distance.text = ""
            binding.toDestinationLabel.visibility = View.INVISIBLE
            UiUtils.longToast(requireContext(), getString(R.string.arrived))
        } else if (d != null){
            mapView.showBeacon(d)
            val distance = Distance(gps.location.distanceTo(d), DistanceUnits.Meters)
            binding.distance.text = "${distance.distance.roundToInt()} m"
            binding.toDestinationLabel.visibility = View.VISIBLE
        } else {
            binding.distance.text = ""
            binding.toDestinationLabel.visibility = View.INVISIBLE
        }

        if (keepCentered){
            mapView.showLocation(gps.location)
        }

    }

    private fun onLocationUpdate(): Boolean {
        compass.declination = geoService.getDeclination(gps.location, gps.altitude)
        if (!wasCentered) {
            mapView.showLocation(gps.location)
            wasCentered = true
        }

        val d = destination
        if (d != null) {
            mapView.showBeacon(d)
        }

        update()
        return true
    }

    private fun onCompassUpdate(): Boolean {
        update()
        return true
    }

}
