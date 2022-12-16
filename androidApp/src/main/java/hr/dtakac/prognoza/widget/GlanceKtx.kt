package hr.dtakac.prognoza.widget

import android.os.Build
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius

fun GlanceModifier.appWidgetBackgroundRadius(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this.cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        this
    }
}