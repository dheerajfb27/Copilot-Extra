package com.example.pixelglassweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.glance.appwidget.updateAll
import com.example.pixelglassweather.widget.WeatherWidget
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent {
   var data by remember { mutableStateOf<WeatherResponse?>(null) }
   var error by remember { mutableStateOf("") }
   var loading by remember { mutableStateOf(false) }
   var allowed by remember { mutableStateOf(hasPermission()) }
   var skin by remember { mutableStateOf("orange") }
   var opacity by remember { mutableFloatStateOf(88f) }
   val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
    allowed = it.values.any { granted -> granted }
    if (allowed) refresh({ loading=it },{ data=it },{ error=it },skin,opacity.toInt())
   }
   MaterialTheme {
    Surface(Modifier.fillMaxSize(), color=Color(0xFF171821)) {
     Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
      Text("Pixel Glass Weather ☀️", style=MaterialTheme.typography.headlineSmall, color=Color.White)
      Text("Glass skin", color=Color.White)
      Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement=Arrangement.spacedBy(6.dp)) {
       listOf("orange","blue","pink","purple","green").forEach { s ->
        FilterChip(selected=skin==s,onClick={skin=s;saveStyle(s,opacity.toInt())},label={Text(s.replaceFirstChar { it.uppercase() })})
       }
      }
      Text("Background opacity: ${opacity.toInt()}%",color=Color.White)
      Slider(value=opacity,onValueChange={opacity=it;saveStyle(skin,it.toInt())},valueRange=0f..100f)
      if (!allowed) Button(onClick={ask.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION))}) { Text("Enable automatic location") }
      else Button(onClick={refresh({loading=it},{data=it},{error=it},skin,opacity.toInt()),enabled=!loading) { Text(if(loading) "Loading…" else "Refresh weather") }
      if(error.isNotBlank()) Text(error,color=Color(0xFFFFB4AB))
      data?.let { w ->
       val c=w.current
       val rain=w.hourly.rain.firstOrNull() ?: 0
       val card=when(skin){"blue"->Color(0xCC075BA5);"pink"->Color(0xCCE32D76);"purple"->Color(0xCC5138B8);"green"->Color(0xCC07834F);else->Color(0xCCF58220)}
       Card(colors=CardDefaults.cardColors(containerColor=card),shape=RoundedCornerShape(28.dp)) {
        Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
         Text("📍 Your current location",color=Color.White)
         Text("${c.temperature.toInt()}°",style=MaterialTheme.typography.displayLarge,color=Color.White)
         Text(description(c.code),style=MaterialTheme.typography.titleLarge,color=Color.White)
         Text("Feels like ${c.feelsLike.toInt()}° · Humidity ${c.humidity}% · Wind ${c.wind.toInt()} km/h",color=Color.White)
         Text(message(c.code,rain),style=MaterialTheme.typography.titleMedium,color=Color.White)
        }
       }
       Text("Hourly forecast",style=MaterialTheme.typography.titleLarge,color=Color.White)
       Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        (0 until minOf(12,w.hourly.time.size)).forEach { i ->
         Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF2A2C39))) {
          Column(Modifier.padding(10.dp)) { Text(w.hourly.time[i].takeLast(5),color=Color.White);Text("${w.hourly.temperature[i].toInt()}°",color=Color.White);Text("☔ ${w.hourly.rain[i]}%",color=Color(0xFF9BD7FF)) }
         }
        }
       }
       Text("7-day forecast",style=MaterialTheme.typography.titleLarge,color=Color.White)
       w.daily.time.indices.forEach { i ->
        Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF2A2C39)),modifier=Modifier.fillMaxWidth()) {
         Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween) {
          Column { Text(w.daily.time[i],color=Color.White);Text("☔ ${w.daily.rain[i]}% rain",color=Color(0xFF9BD7FF)) }
          Text("${w.daily.high[i].toInt()}° / ${w.daily.low[i].toInt()}°",color=Color.White)
         }
        }
       }
       Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF2A2C39)),modifier=Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween) {
         Column { Text("🌅 Sunrise",color=Color.White);Text(w.daily.sunrise.firstOrNull()?.takeLast(5) ?: "--",color=Color.White) }
         Column { Text("🌇 Sunset",color=Color.White);Text(w.daily.sunset.firstOrNull()?.takeLast(5) ?: "--",color=Color.White) }
         Column { Text("UV max",color=Color.White);Text(w.daily.uv.firstOrNull()?.toInt()?.toString() ?: "--",color=Color.White) }
        }
       }
      }
     }
    }
   }
  }
 }
 private fun hasPermission() =
  ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
  ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED

 private fun saveStyle(skin:String,opacity:Int) {
  getSharedPreferences("weather_widget",MODE_PRIVATE).edit().putString("skin",skin).putInt("opacity",opacity).apply()
  lifecycleScope.launch { runCatching { WeatherWidget().updateAll(applicationContext) } }
 }
 private fun refresh(onLoading:(Boolean)->Unit,onData:(WeatherResponse)->Unit,onError:(String)->Unit,skin:String,opacity:Int) {
  lifecycleScope.launch {
   onLoading(true);onError("")
   try {
    val client=LocationServices.getFusedLocationProviderClient(this@MainActivity)
    val token=CancellationTokenSource()
    @Suppress("MissingPermission")
    val loc:Location = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,token.token).await()
     ?: throw IllegalStateException("Location unavailable. Turn on location and retry.")
    val w=WeatherService.api.forecast(loc.latitude,loc.longitude)
    onData(w)
    val rain=w.hourly.rain.firstOrNull() ?: 0
    getSharedPreferences("weather_widget",MODE_PRIVATE).edit()
     .putString("temperature","${w.current.temperature.toInt()}°")
     .putString("city","Local weather").putString("condition",description(w.current.code))
     .putString("message",message(w.current.code,rain)).putString("skin",skin).putInt("opacity",opacity)
     .putString("latitude",loc.latitude.toString()).putString("longitude",loc.longitude.toString()).apply()
    WeatherWidget().updateAll(applicationContext)
   } catch(e:Exception) { onError(e.localizedMessage ?: "Weather unavailable. Check internet and location.") }
   finally { onLoading(false) }
  }
 }
}
