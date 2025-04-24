package com.example.myapplication.ui.admin.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.model.User

class UsersAdapter(private var userList: List<User>) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.textName)
        val typeText: TextView = itemView.findViewById(R.id.typeTv)
        val companyText: TextView = itemView.findViewById(R.id.textCompany)
        val positionText: TextView = itemView.findViewById(R.id.textPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.nameText.text = user.name
        holder.typeText.text = user.user_type
        holder.companyText.text = user.company
        holder.positionText.text = user.position
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}