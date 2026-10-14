package com.example.pixelglassweather.widget

import android.content.Context
import android.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class WeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
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
        val bg = Color.argb(opacity, Color.red(base), Color.green(base), Color.blue(base))

        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().background(ColorProvider(bg)).padding(16.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "$city  •  $temperature",
                    style = TextStyle(
                        color = ColorProvider(Color.WHITE),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = condition,
                    style = TextStyle(
                        color = ColorProvider(Color.WHITE),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = message,
                    style = TextStyle(color = ColorProvider(Color.WHITE), fontSize = 11.sp)
                )
            }
        }
    }
}
