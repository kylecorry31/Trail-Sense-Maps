package com.kylecorry.trail_sense_maps.maps.domain

import com.kylecorry.trailsensecore.domain.geo.Bearing
import com.kylecorry.trailsensecore.domain.geo.CompassDirection
import com.kylecorry.trailsensecore.domain.geo.Coordinate

data class BoundingArea(val topLeft: Coordinate, val bottomRight: Coordinate) {

    val topRight = Coordinate(topLeft.latitude, bottomRight.longitude)
    val bottomLeft = Coordinate(bottomRight.latitude, topLeft.longitude)

    companion object {
        fun fromCenter(
            center: Coordinate,
            distanceEast: Double,
            distanceNorth: Double
        ): BoundingArea {
            return BoundingArea(
                center.plus(distanceEast, Bearing(CompassDirection.West.azimuth))
                    .plus(distanceNorth, Bearing(CompassDirection.North.azimuth)),
                center.plus(distanceEast, Bearing(CompassDirection.East.azimuth))
                    .plus(distanceNorth, Bearing(CompassDirection.South.azimuth))
            )
        }
    }

}