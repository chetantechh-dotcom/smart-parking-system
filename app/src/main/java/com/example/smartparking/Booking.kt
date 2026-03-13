package com.example.smartparking

import com.google.firebase.Timestamp

data class Booking(
    val slot: String = "",
    val vehicleNumber: String = "",
    val bookingTime: Timestamp = Timestamp.now()
)