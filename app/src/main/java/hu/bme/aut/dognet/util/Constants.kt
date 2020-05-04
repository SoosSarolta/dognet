package hu.bme.aut.dognet.util

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/// Constants used in the application

const val SPLASH_TIME_OUT: Long = 3000

const val REQUEST_CODE_CAMERA = 1000
const val REQUEST_CODE_STORAGE = 2000

const val VET_FIREBASE_ENTRY: String = "vetDbEntry"
const val TRAINER_FIREBASE_ENTRY: String = "trainerDbEntry"
const val LOST_FIREBASE_ENTRY: String = "lostDbEntry"

val DB: DatabaseReference = FirebaseDatabase.getInstance().reference