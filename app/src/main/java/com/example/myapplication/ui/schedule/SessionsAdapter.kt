package com.example.myapplication.ui.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.model.Session
import java.text.SimpleDateFormat
import java.util.Locale

class SessionsAdapter(private var sessionsList: List<Session>,
                      private val onItemClick: (Session) -> Unit,
                      private val onAddSessionClick: (Session) -> Unit
) : RecyclerView.Adapter<SessionsAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvRoom: TextView = itemView.findViewById(R.id.tvRoom)
        val btnAddEvent: AppCompatImageButton = itemView.findViewById(R.id.btnAddEvent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).
            inflate(R.layout.item_session, parent, false)
        return SessionViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessionsList[position]
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTime = session.start_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"
        val endTime = session.end_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"

        holder.tvTime.text = "$startTime - $endTime "
        holder.tvTitle.text = session.title
        holder.tvRoom.text = session.room

        holder.itemView.setOnClickListener {
            onItemClick(session)
        }

        holder.btnAddEvent.setOnClickListener {
            onAddSessionClick(session)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newSessions: List<Session>) {
        sessionsList = newSessions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = sessionsList.size

}

