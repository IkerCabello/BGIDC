package com.example.myapplication.ui.speakers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.model.Session
import java.text.SimpleDateFormat
import java.util.Locale

class SimpleSessionAdapter (private val sessions: List<Session>) :
    RecyclerView.Adapter<SimpleSessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.textTitle)
        val tvRoom: TextView = itemView.findViewById(R.id.textRoom)
        val tvTime: TextView = itemView.findViewById(R.id.textTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session_speaker, parent, false)
        return SessionViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]

        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedStartTime = session.start_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"
        val formattedEndTime = session.end_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"

        holder.tvTitle.text = session.title
        holder.tvRoom.text = session.room
        holder.tvTime.text = "$formattedStartTime - $formattedEndTime"

    }

    override fun getItemCount(): Int = sessions.size

}