package com.kylecorry.trail_sense_maps.maps.infrastructure

import android.content.Context
import android.content.Intent
import com.kylecorry.trail_sense_maps.R
import com.kylecorry.trailsensecore.domain.geo.Coordinate
import com.kylecorry.trailsensecore.infrastructure.system.IntentUtils

object ShareService {

    fun shareLocation(context: Context, location: Coordinate){
        val intent = IntentUtils.geo(location)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val chooser = Intent.createChooser(intent, context.getString(R.string.share_location))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooser)
        }
    }

}