package hu.bme.aut.dognet.util

import android.app.Activity
import android.content.Intent

/// Intents used in the application

fun intentStartActivity(activityFrom: Activity, activityTo: Activity) {
    val intent = Intent(activityFrom, activityTo::class.java)
    activityFrom.startActivity(intent)
}