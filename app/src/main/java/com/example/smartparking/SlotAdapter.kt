package com.example.smartparking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SlotAdapter(
    private val slots: List<String>,
    private val bookedSlots: Set<String>,
    private val onSlotClick: (String) -> Unit
) : RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSlot: TextView = itemView.findViewById(R.id.tvSlot)
        val ivFire: ImageView = itemView.findViewById(R.id.ivFire)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.tvSlot.text = slot

        if (bookedSlots.contains(slot)) {
            // Booked: Red, non-clickable, 🔥 icon
            holder.itemView.setBackgroundColor(android.graphics.Color.RED)
            holder.tvSlot.setTextColor(android.graphics.Color.WHITE)
            holder.ivFire.visibility = View.VISIBLE
            holder.itemView.isClickable = false
        } else {
            // Available: Green, clickable
            holder.itemView.setBackgroundColor(android.graphics.Color.GREEN)
            holder.tvSlot.setTextColor(android.graphics.Color.WHITE)
            holder.ivFire.visibility = View.GONE
            holder.itemView.setOnClickListener { onSlotClick(slot) }
        }
    }

    override fun getItemCount() = slots.size
}