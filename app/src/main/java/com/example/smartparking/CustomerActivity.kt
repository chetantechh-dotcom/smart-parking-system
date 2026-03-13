package com.example.smartparking

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CustomerActivity : AppCompatActivity() {

    private lateinit var slotAdapter: SlotAdapter
    private val db = FirebaseFirestore.getInstance()
    private val slots = listOf(
        "A1", "A2", "A3", "A4",
        "B1", "B2", "B3", "B4",
        "C1", "C2", "C3", "C4"
    )
    private val bookedSlots = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_slots)
        setupRecyclerView()
        loadBookedSlots()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerSlots)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        slotAdapter = SlotAdapter(slots, bookedSlots) { slot ->
            if (!bookedSlots.contains(slot)) {
                bookSlot(slot)
            }
        }
        recyclerView.adapter = slotAdapter
    }

    private fun loadBookedSlots() {
        db.collection("bookings")
            .addSnapshotListener { snapshot, _ ->
                bookedSlots.clear()
                val now = Timestamp.now()
                snapshot?.documents?.forEach { doc ->
                    val booking = doc.toObject(Booking::class.java)
                    booking?.let {
                        val hoursDiff = (now.seconds - it.bookingTime.seconds) / 3600
                        if (hoursDiff >= 1) {
                            db.collection("bookings").document(it.slot).delete()
                            showBookingNotification(it.slot)
                        } else {
                            bookedSlots.add(it.slot)
                        }
                    }
                }
                slotAdapter.notifyDataSetChanged()
            }
    }

    private fun bookSlot(slot: String) {
        val vehicleInput = EditText(this).apply { hint = "Enter Vehicle Number" }
        AlertDialog.Builder(this)
            .setTitle("Book Slot $slot")
            .setView(vehicleInput)
            .setPositiveButton("Proceed to Payment") { _, _ ->
                val vehicleNumber = vehicleInput.text.toString().trim()
                if (vehicleNumber.isNotEmpty()) {
                    showDemoPayment(slot, vehicleNumber)
                } else {
                    Toast.makeText(this, "Enter vehicle number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDemoPayment(slot: String, vehicleNumber: String) {
        AlertDialog.Builder(this)
            .setTitle("Demo Payment")
            .setMessage("Fake payment processing... Click to confirm.")
            .setPositiveButton("Pay Now (Demo)") { _, _ ->
                val booking = Booking(slot, vehicleNumber)
                db.collection("bookings").document(slot)
                    .set(booking, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Slot $slot booked successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Booking failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBookingNotification(slot: String) {
        AlertDialog.Builder(this)
            .setTitle("Booking Completed")
            .setMessage("Booking for slot $slot is 1 hour complete!")
            .setPositiveButton("OK", null)
            .show()
    }
}
