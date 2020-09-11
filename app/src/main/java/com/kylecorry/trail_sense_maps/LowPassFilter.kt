package com.kylecorry.trail_sense_maps

class LowPassFilter(private val alpha: Float, initialValue: Float = 0f) {

    private var estimate = initialValue

    fun filter(measurement: Float): Float {
        estimate = (1 - alpha) * estimate + alpha * measurement
        return estimate
    }
}