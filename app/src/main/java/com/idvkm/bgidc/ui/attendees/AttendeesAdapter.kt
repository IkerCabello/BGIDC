package com.idvkm.bgidc.ui.attendees

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.idvkm.bgidc.R
import com.idvkm.bgidc.ui.model.User

class AttendeesAdapter(private val attendeesList: List<User>, private val onItemClick: (User) -> Unit) :
    RecyclerView.Adapter<AttendeesAdapter.AttendeeViewHolder>() {

    class AttendeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.textName)
        val company: TextView = itemView.findViewById(R.id.textCompany)
        val position: TextView = itemView.findViewById(R.id.textPosition)
        val image: ImageView = itemView.findViewById(R.id.imageSpeaker)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendee, parent, false)
        return AttendeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        val attendee = attendeesList[position]
        holder.name.text = attendee.name
        holder.company.text = attendee.company
        holder.position.text = attendee.position

        Glide.with(holder.itemView.context)
            .load(attendee.profile_img)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(attendee)
        }
    }

    override fun getItemCount() = attendeesList.size
}
