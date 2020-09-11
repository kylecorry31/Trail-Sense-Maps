package com.kylecorry.trail_sense_maps.sensors

import android.hardware.GeomagneticField
import com.kylecorry.trail_sense_maps.maps.domain.Coordinate

class DeclinationCalculator : IDeclinationCalculator {
    override fun calculate(location: Coordinate, altitude: Float): Float {
        val time: Long = System.currentTimeMillis()
        val geoField = GeomagneticField(location.latitude.toFloat(), location.longitude.toFloat(), altitude, time)
        return geoField.declination
    }
}