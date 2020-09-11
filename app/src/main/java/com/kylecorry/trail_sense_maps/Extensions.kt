package com.kylecorry.trail_sense_maps

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

inline fun FragmentManager.doTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitAllowingStateLoss()
}

fun Fragment.switchToFragment(fragment: Fragment, holderId: Int = R.id.fragment_holder, addToBackStack: Boolean = false) {
    parentFragmentManager.doTransaction {
        if (addToBackStack) {
            this.addToBackStack(null)
        }
        this.replace(
            holderId,
            fragment
        )
    }
}

fun Double.roundPlaces(places: Int): Double {
    return (this * 10.0.pow(places)).roundToInt() / 10.0.pow(places)
}

fun Float.roundPlaces(places: Int): Float {
    return (this * 10f.pow(places)).roundToInt() / 10f.pow(places)
}

fun deltaAngle(angle1: Float, angle2: Float): Float {
    var delta = angle2 - angle1
    delta += 180
    delta -= floor(delta / 360) * 360
    delta -= 180
    if (abs(abs(delta) - 180) <= Float.MIN_VALUE) {
        delta = 180f
    }
    return delta
}

fun Float.toDegrees(): Float {
    return Math.toDegrees(this.toDouble()).toFloat()
}

fun normalizeAngle(angle: Float): Float {
    var outputAngle = angle
    while (outputAngle < 0) outputAngle += 360
    return outputAngle % 360
}

fun normalizeAngle(angle: Double): Double {
    var outputAngle = angle
    while (outputAngle < 0) outputAngle += 360
    return outputAngle % 360
}