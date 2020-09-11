package com.kylecorry.trail_sense_maps.sensors

import com.kylecorry.trail_sense_maps.maps.domain.Bearing

interface ICompass: ISensor {
    val bearing: Bearing
    var declination: Float

    fun setSmoothing(smoothing: Int)
}