package com.example.smartparking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GuardActivity : AppCompatActivity() {
    private lateinit var bookingAdapter: BookingAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guard)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerBookings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter with isGuard=true for Delete + Reserve functionality
        bookingAdapter = BookingAdapter(emptyList(), isGuard = true)
        recyclerView.adapter = bookingAdapter

        loadBookings()
    }

    private fun loadBookings() {
        db.collection("bookings")
            .orderBy("bookingTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                val now = Timestamp.now()
                bookings.forEach { booking ->
                    val hoursDiff = (now.seconds - booking.bookingTime.seconds) / 3600
                    if (hoursDiff >= 1) {
                        showBookingNotification(booking.slot)
                    }
                }
                bookingAdapter.updateBookings(bookings)
            }
    }

    private fun showBookingNotification(slot: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Booking Completed")
            .setMessage("Booking for slot $slot is 1 hour complete!")
            .setPositiveButton("OK", null)
            .show()
    }
}
