# Pixel Glass Weather

Native Android weather app and 4×1 glass-style home-screen widget.

## Features
- Kotlin, Jetpack Compose and Jetpack Glance
- Open-Meteo live forecast (no API key for eligible non-commercial use; review provider terms)
- Runtime permission and automatic device location
- Current conditions, hourly rain probability, 7-day highs/lows, sunrise/sunset, humidity, wind and feels-like
- Orange, blue, pink, purple and green widget skins
- Opacity slider including transparent background option
- Weather-aware playful messages and umbrella reminders

## Build
1. Install Android Studio with Android SDK 35 and JDK 17.
2. Open this repository root in Android Studio and sync Gradle.
3. Run the app on an Android 8.0+ device/emulator and grant location permission.
4. Add Pixel Glass Weather from the home-screen widget picker.

A Gradle wrapper is not included; Android Studio can build the project using its Gradle integration. Before release, test on real launchers and add a wrapper.

## Limitations
Widget transparency varies by launcher; true wallpaper blur is not universally supported. Background periodic refresh and official severe-weather alerts are not implemented yet. Rain probability is a forecast, not an official warning. This starter has not been build-tested in this environment.

Weather source: https://open-meteo.com/en/docs
Terms: https://open-meteo.com/en/terms
