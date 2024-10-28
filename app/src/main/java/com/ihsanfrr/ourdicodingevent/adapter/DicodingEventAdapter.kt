package com.ihsanfrr.ourdicodingevent.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ihsanfrr.ourdicodingevent.R
import com.ihsanfrr.ourdicodingevent.data.local.entity.DicodingEventEntity
import com.ihsanfrr.ourdicodingevent.helpers.DateHelper
import com.ihsanfrr.ourdicodingevent.ui.MainViewModel
import com.ihsanfrr.ourdicodingevent.ui.detail.DetailActivity

class DicodingEventAdapter(private val viewModel: MainViewModel, private val isList: Boolean) : RecyclerView.Adapter<DicodingEventAdapter.ListEventViewHolder>() {

    private val events = mutableListOf<DicodingEventEntity>()

    fun setEvents(newEvents: List<DicodingEventEntity>) {
        val diffCallback = EventDiffCallback(events, newEvents)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        events.clear()
        events.addAll(newEvents)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(if(isList) R.layout.list_dicoding_event else R.layout.grid_dicoding_event, parent, false)
        return ListEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListEventViewHolder, position: Int) {
        val event: DicodingEventEntity = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class ListEventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val eventName: TextView = view.findViewById(R.id.tv_event_name)
        private val eventOwner: TextView = view.findViewById(R.id.tv_event_ownerName)
        private val eventBeginTime: TextView = view.findViewById(R.id.tv_event_beginTime)
        private val eventQuota: TextView = view.findViewById(R.id.tv_event_quota)
        private val eventImage: ImageView = view.findViewById(R.id.iv_image_logo)
        private val btnFavorite: Button = view.findViewById(R.id.btn_favorite)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(event: DicodingEventEntity) {
            eventName.text = event.name
            eventOwner.text = event.ownerName
            eventBeginTime.text = DateHelper.format(event.beginTime)
            "${event.registrants}/${event.quota}".also { eventQuota.text = it }

            Glide.with(view.context)
                .load(event.imageLogo)
                .into(eventImage)

            view.setOnClickListener{
                val intent = Intent(view.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
                view.context.startActivity(intent)
            }

            if (event.isFavorite) {
                btnFavorite.background = view.context.getDrawable(R.drawable.baseline_favorite_24)
            } else {
                btnFavorite.background = view.context.getDrawable(R.drawable.baseline_unfavorite_24)
            }

            btnFavorite.setOnClickListener {
                event.isFavorite = !event.isFavorite
                btnFavorite.background = if (event.isFavorite) {
                    view.context.getDrawable(R.drawable.baseline_favorite_24)
                } else {
                    view.context.getDrawable(R.drawable.baseline_unfavorite_24)
                }

                viewModel.updateFavoriteStatus(event, event.isFavorite)
            }
        }
    }

    private class EventDiffCallback(
        private val oldList: List<DicodingEventEntity>,
        private val newList: List<DicodingEventEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id // Compare unique IDs
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition] // Check for content equality
        }
    }
}