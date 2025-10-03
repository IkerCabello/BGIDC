package com.idvkm.bgidc.ui.speakers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.idvkm.bgidc.R
import com.idvkm.bgidc.ui.model.User

class SpeakersAdapter(private val speakersList: List<User>, private val onItemClick: (User) -> Unit) :
    RecyclerView.Adapter<SpeakersAdapter.SpeakerViewHolder>() {

    class SpeakerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.textName)
        val company: TextView = itemView.findViewById(R.id.textCompany)
        val position: TextView = itemView.findViewById(R.id.textPosition)
        val image: ImageView = itemView.findViewById(R.id.imageSpeaker)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_speaker, parent, false)
        return SpeakerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        val speaker = speakersList[position]
        holder.name.text = speaker.name
        holder.company.text = speaker.company
        holder.position.text = speaker.position

        Glide.with(holder.itemView.context)
            .load(speaker.profile_img)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(speaker)
        }
    }

    override fun getItemCount() = speakersList.size
}
