package com.kylecorry.trail_sense_maps.maps.ui

import android.content.Context
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.kylecorry.trail_sense_maps.R
import com.kylecorry.trailsensecore.domain.geo.Coordinate
import com.kylecorry.trailsensecore.infrastructure.system.UiUtils
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class CustomMapView(private val map: MapView, private val compass: ImageView, startingLocation: Coordinate? = null) {

    private var marker: Marker? = null
    private var line: Polyline? = null
    private val myLocationMarker: Marker
    private var myLocation: Coordinate? = null

    var onLocationSelected: ((location: Coordinate) -> Unit)? = null
    var onLocationClick: ((location: Coordinate) -> Unit)? = null

    init {
        if (startingLocation != null) {
            showLocation(startingLocation,
                defaultZoom
            )
        }

        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
//        map.minZoomLevel = minZoom
        map.controller.zoomTo(defaultZoom)
        map.maxZoomLevel = maxZoom
        map.isFlingEnabled = true

        map.setTileSource(TileSourceFactory.OpenTopo)
//        map.tileProvider.setUseDataConnection(false)



        myLocationMarker = Marker(map)
        myLocationMarker.icon = UiUtils.drawable(map.context, R.drawable.ic_location)?.apply {
            setTint(UiUtils.color(map.context, R.color.colorPrimary))
        }
        myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(myLocationMarker)

        val mapEventReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p ?: return true
                val location = Coordinate(p.latitude, p.longitude)
                onLocationClick?.invoke(location)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                p ?: return true
                val location = Coordinate(p.latitude, p.longitude)
                onLocationSelected?.invoke(location)
                return true
            }
        }

        map.overlays.add(MapEventsOverlay(mapEventReceiver))
    }

    fun showLocation(location: Coordinate, zoom: Double = defaultZoom){
        map.controller.setZoom(zoom)
        map.controller.setCenter(GeoPoint(location.latitude, location.longitude))
    }

    fun showBeacon(location: Coordinate){
        hideBeacon()
        marker = Marker(map)
        marker?.icon = UiUtils.drawable(map.context, R.drawable.ic_location)?.apply {
            setTint(UiUtils.color(map.context, R.color.colorPrimary))
        }
        marker?.position = GeoPoint(location.latitude, location.longitude)
        marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)

        val myLoc = myLocation
        if (myLoc != null) {
            line = Polyline()
            line?.setPoints(listOf(GeoPoint(myLoc.latitude, myLoc.longitude), GeoPoint(location.latitude, location.longitude)))
            line?.outlinePaint?.color = UiUtils.color(map.context, R.color.colorPrimary)
            map.overlays.add(line)
        }

    }

    fun hideBeacon(){
        if (marker != null){
            map.overlays.remove(marker)
            marker = null
        }

        if (line != null){
            map.overlays.remove(line)
            line = null
        }
    }

    fun setMapAzimuth(azimuth: Float){
        map.mapOrientation = -azimuth
    }

    fun setCompassAzimuth(azimuth: Float){
        compass.rotation = -azimuth
    }

    fun setMyLocationAzimuth(azimuth: Float?){
        if (azimuth != null) {
            myLocationMarker.rotation = -azimuth
            myLocationMarker.icon = UiUtils.drawable(map.context, R.drawable.ic_navigation)?.apply {
                setTint(UiUtils.color(map.context, R.color.colorPrimary))
            }
        } else {
            myLocationMarker.icon = UiUtils.drawable(map.context, R.drawable.ic_my_location)?.apply {
                setTint(UiUtils.color(map.context, R.color.colorPrimary))
            }
        }
    }

    fun setMyLocation(location: Coordinate){
        myLocationMarker.position = GeoPoint(location.latitude, location.longitude)
        myLocation = location
    }

    fun downloadCurrentMap(){
        // TODO: Download the current displayed tiles
    }

    fun setVisibility(visibility: Int){
        map.visibility = visibility
        compass.visibility = visibility
    }

    fun onResume(){
        map.onResume()
    }

    fun onPause(){
        map.onPause()
    }

    companion object {
        private const val defaultZoom = 15.0
        private const val minZoom = 15.0
        private const val maxZoom = 17.5

        fun configure(context: Context?){
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        }

    }

}