package com.example.myapplication.ui.admin.sessions

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.model.Session
import java.text.SimpleDateFormat
import java.util.*

class SessionAdapter(
    private var sessions: List<Session>,
    private val onEditClick: (Session) -> Unit,
    private val onDeleteClick: (Session) -> Unit
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session_admin, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]

        val start = session.start_time?.toDate()
        val end = session.end_time?.toDate()
        val timeText = if (start != null && end != null)
            "${formatter.format(start)} - ${formatter.format(end)} CET"
        else
            "Time not set"

        holder.tvTime.text = timeText
        holder.tvTitle.text = session.title
        holder.tvRoom.text = session.room

        holder.btnEdit.setOnClickListener {
            onEditClick(session)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(session)
        }
    }

    override fun getItemCount() = sessions.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Session>) {
        sessions = newList
        notifyDataSetChanged()
    }

    class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvRoom: TextView = view.findViewById(R.id.tvRoom)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditSession)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteSession)
    }
}