package com.kylecorry.trail_sense_maps.sensors


interface ISensor {

    val accuracy: Accuracy

    val hasValidReading: Boolean

    fun start(listener: SensorListener)

    fun stop(listener: SensorListener?)

}