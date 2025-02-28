package com.hasan.eventapp.presentation.events.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.hasan.eventapp.R


class EventSkeletonAdapter(private val itemCount: Int = DEFAULT_ITEM_COUNT) :
    RecyclerView.Adapter<EventSkeletonAdapter.SkeletonViewHolder>() {

    // ===========================
    // Adapter Implementation
    // ===========================
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        return SkeletonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event_skeleton, parent, false)
        )
    }

    override fun getItemCount(): Int = itemCount

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
        holder.startShimmer()
    }

    // ===========================
    // ViewHolder
    // ===========================
    class SkeletonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun startShimmer() {
            val animation = AnimationUtils.loadAnimation(
                itemView.context,
                R.anim.skeleton_animation
            )
            itemView.startAnimation(animation)
        }
    }

    // ===========================
    // Constants
    // ===========================
    companion object {
        private const val DEFAULT_ITEM_COUNT = 5
    }
}