package com.hasan.eventapp.presentation.events.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hasan.eventapp.R
import com.hasan.eventapp.databinding.ItemEventBinding
import com.hasan.eventapp.domain.models.EventDomain
import com.hasan.eventapp.utils.extensions.formatPrice


class EventAdapter(private val onEventClick: (EventDomain) -> Unit) :
    ListAdapter<EventDomain, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    // ===========================
    // Adapter Implementation
    // ===========================
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            ItemEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()


    // ===========================
    // ViewHolder
    // ===========================
    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventDomain) {
            setupEventInfo(event)
            loadEventImage(event)
            setupClickListeners(event)
        }

        private fun setupEventInfo(event: EventDomain) {
            with(binding) {
                eventTitle.text = event.title
                eventDescription.text = event.description
                eventDateLabel.text = event.date
                eventPrice.text = event.price.formatPrice()
            }
        }

        private fun loadEventImage(event: EventDomain) {
            Glide.with(itemView.context)
                .load(event.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .into(binding.eventImage)
        }

        private fun setupClickListeners(event: EventDomain) {
            binding.root.setOnClickListener { onEventClick(event) }
            binding.bookButton.setOnClickListener { onEventClick(event) }
        }
    }

    // ===========================
    // DiffUtil Callback
    // ===========================
    class EventDiffCallback : DiffUtil.ItemCallback<EventDomain>() {
        override fun areItemsTheSame(oldItem: EventDomain, newItem: EventDomain): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EventDomain, newItem: EventDomain): Boolean =
            oldItem == newItem
    }
}