package com.example.pixelglassweather.widget

import android.content.Context
import android.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.defaultWeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.text.FontWeight
import androidx.glance.unit.dp
import androidx.glance.unit.sp

class WeatherWidget : GlanceAppWidget() {
 override suspend fun provideGlance(context: Context, id: GlanceId) {
  val p = context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
  val temp = p.getString("temperature", "--°") ?: "--°"
  val city = p.getString("city", "Local weather") ?: "Local weather"
  val cond = p.getString("condition", "Open app to refresh") ?: "Open app to refresh"
  val msg = p.getString("message", "Have a lovely day 😊") ?: "Have a lovely day 😊"
  val skin = p.getString("skin", "orange") ?: "orange"
  val alpha = p.getInt("opacity", 88).coerceIn(0,100) * 255 / 100
  val base = when(skin) { "blue" -> Color.rgb(0,105,190); "pink" -> Color.rgb(225,42,112); "purple" -> Color.rgb(79,55,170); "green" -> Color.rgb(0,130,78); else -> Color.rgb(245,120,20) }
  val bg = Color.argb(alpha, Color.red(base), Color.green(base), Color.blue(base))
  provideContent {
   Row(modifier = GlanceModifier.fillMaxSize().background(ColorProvider(bg)).padding(10.dp),
    verticalAlignment = Alignment.Vertical.CenterVertically) {
    Column(modifier = GlanceModifier.defaultWeight(), verticalAlignment = Alignment.Vertical.CenterVertically) {
     Text(city, style = TextStyle(color = ColorProvider(Color.WHITE), fontSize = 11.sp))
     Text(temp, style = TextStyle(color = ColorProvider(Color.WHITE), fontSize = 28.sp, fontWeight = FontWeight.Bold))
    }
    Column(modifier = GlanceModifier.defaultWeight(), verticalAlignment = Alignment.Vertical.CenterVertically) {
     Text(cond, style = TextStyle(color = ColorProvider(Color.WHITE), fontSize = 12.sp, fontWeight = FontWeight.Bold))
     Text(msg, style = TextStyle(color = ColorProvider(Color.WHITE), fontSize = 10.sp))
    }
   }
  }
 }
}
