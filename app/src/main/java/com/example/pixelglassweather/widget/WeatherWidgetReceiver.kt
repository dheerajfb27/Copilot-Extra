package com.example.pixelglassweather.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews
import com.example.pixelglassweather.R

class WeatherWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, WeatherWidgetReceiver::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isNotEmpty()) updateWidgets(context, manager, ids)
        }

        private fun updateWidgets(
            context: Context,
            manager: AppWidgetManager,
            ids: IntArray
        ) {
            val prefs = context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
            val temperature = prefs.getString("temperature", "--°") ?: "--°"
            val city = prefs.getString("city", "Local weather") ?: "Local weather"
            val condition = prefs.getString("condition", "Open app to refresh") ?: "Open app to refresh"
            val message = prefs.getString("message", "Have a lovely day") ?: "Have a lovely day"
            val skin = prefs.getString("skin", "orange") ?: "orange"
            val opacity = prefs.getInt("opacity", 88).coerceIn(0, 100) * 255 / 100
            val base = when (skin) {
                "blue" -> Color.rgb(0, 105, 190)
                "pink" -> Color.rgb(225, 42, 112)
                "purple" -> Color.rgb(79, 55, 170)
                "green" -> Color.rgb(0, 130, 78)
                else -> Color.rgb(245, 120, 20)
            }
            val background = Color.argb(opacity, Color.red(base), Color.green(base), Color.blue(base))
            ids.forEach { id ->
                val views = RemoteViews(context.packageName, R.layout.widget_weather)
                views.setTextViewText(R.id.widget_city_temp, "$city  •  $temperature")
                views.setTextViewText(R.id.widget_condition, condition)
                views.setTextViewText(R.id.widget_message, message)
                views.setInt(R.id.weather_widget_root, "setBackgroundColor", background)
                manager.updateAppWidget(id, views)
            }
        }
    }
}
