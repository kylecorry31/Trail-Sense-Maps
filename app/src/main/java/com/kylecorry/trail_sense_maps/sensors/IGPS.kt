package com.kylecorry.trail_sense_maps.sensors

import com.kylecorry.trail_sense_maps.maps.domain.Coordinate

interface IGPS: ISensor, IAltimeter {
    val location: Coordinate
    val speed: Float
    val verticalAccuracy: Float?
    val horizontalAccuracy: Float?
    val satellites: Int
}