package com.kylecorry.trail_sense_maps.maps.infrastructure

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kylecorry.trailsensecore.domain.geo.Coordinate
import com.kylecorry.trailsensecore.infrastructure.system.IntentUtils
import com.kylecorry.trailsensecore.infrastructure.system.PackageUtils

object TrailSense {

    fun open(context: Context) {
        if (!isInstalled(context)) return
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun createBeacon(context: Context, location: Coordinate){
        if (!isInstalled(context)) return
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("geo:${location.latitude},${location.longitude}")
        context.startActivity(intent)
    }

    fun isInstalled(context: Context): Boolean {
        return PackageUtils.isPackageInstalled(context, PACKAGE)
    }

    private const val PACKAGE = "com.kylecorry.trail_sense"

}