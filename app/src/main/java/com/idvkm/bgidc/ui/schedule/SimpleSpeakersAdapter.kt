package com.idvkm.bgidc.ui.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.idvkm.bgidc.R
import com.idvkm.bgidc.ui.model.User

class SimpleSpeakersAdapter(private val speakersList: List<User>) :
    RecyclerView.Adapter<SimpleSpeakersAdapter.SpeakerViewHolder>() {

    class SpeakerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgSpeaker: ImageView = itemView.findViewById(R.id.imageSpeaker)
        val tvName: TextView = itemView.findViewById(R.id.textName)
        val tvPosition: TextView = itemView.findViewById(R.id.textPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_speaker_session, parent, false)
        return SpeakerViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        val speaker = speakersList[position]

        holder.tvName.text = speaker.name
        holder.tvPosition.text = "${speaker.position} at ${speaker.company}"
        Glide.with(holder.itemView.context)
            .load(speaker.profile_img)
            .into(holder.imgSpeaker)
    }

    override fun getItemCount(): Int = speakersList.size
}

