package com.example.smartparking

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BookingAdapter(
    private var bookings: List<Booking>,
    private val isGuard: Boolean = false
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val reservedSlots = mutableSetOf<String>() // Local cache for reserved slots

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSlot: TextView = itemView.findViewById(R.id.tvSlot)
        val tvVehicle: TextView = itemView.findViewById(R.id.tvVehicle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnDelete: Button? = itemView.findViewById(R.id.btnDelete)
        val btnReserve: Button? = itemView.findViewById(R.id.btnReserve)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvSlot.text = "Slot: ${booking.slot}"
        holder.tvVehicle.text = "Vehicle: ${booking.vehicleNumber}"
        holder.tvTime.text = "Time: ${booking.bookingTime.toDate()}"

        if (isGuard) {
            // Delete button
            holder.btnDelete?.visibility = View.VISIBLE
            holder.btnDelete?.setOnClickListener {
                db.collection("bookings").document(booking.slot)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Booking for ${booking.slot} deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(holder.itemView.context, "Failed to delete booking", Toast.LENGTH_SHORT).show()
                    }
            }

            // Reserve / Unreserve button
            holder.btnReserve?.visibility = View.VISIBLE
            if (reservedSlots.contains(booking.slot)) {
                holder.btnReserve?.text = "Unreserve"
                holder.itemView.setBackgroundColor(Color.parseColor("#FFD700")) // Yellow for reserved
            } else {
                holder.btnReserve?.text = "Reserve"
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            holder.btnReserve?.setOnClickListener {
                if (reservedSlots.contains(booking.slot)) {
                    // Unreserve
                    reservedSlots.remove(booking.slot)
                    holder.btnReserve.text = "Reserve"
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                    Toast.makeText(holder.itemView.context, "${booking.slot} unreserved", Toast.LENGTH_SHORT).show()
                } else {
                    // Reserve
                    reservedSlots.add(booking.slot)
                    holder.btnReserve.text = "Unreserve"
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFD700"))
                    Toast.makeText(holder.itemView.context, "${booking.slot} reserved", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            holder.btnDelete?.visibility = View.GONE
            holder.btnReserve?.visibility = View.GONE
        }
    }

    override fun getItemCount() = bookings.size

    fun updateBookings(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
