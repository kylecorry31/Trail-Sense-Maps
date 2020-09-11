package com.kylecorry.trail_sense_maps.sensors

import com.kylecorry.trail_sense_maps.Vector3

interface IAccelerometer: ISensor {
    val acceleration: Vector3
}