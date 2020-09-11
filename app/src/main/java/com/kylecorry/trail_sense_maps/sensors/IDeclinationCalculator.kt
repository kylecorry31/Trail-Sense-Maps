package com.kylecorry.trail_sense_maps.sensors

import com.kylecorry.trail_sense_maps.maps.domain.Coordinate

interface IDeclinationCalculator {
    fun calculate(location: Coordinate, altitude: Float): Float
}