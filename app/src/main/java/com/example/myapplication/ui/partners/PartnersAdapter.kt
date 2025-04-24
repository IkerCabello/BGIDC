package com.example.myapplication.ui.partners

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.model.Partner

class PartnersAdapter(private val partnersList: List<Partner>) :
    RecyclerView.Adapter<PartnersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = view.findViewById(R.id.partnerLogo)
        val name: TextView = view.findViewById(R.id.partnerName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_partner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partner = partnersList[position]
        holder.name.text = partner.name
        holder.logo.setImageResource(partner.logoResId)
    }

    override fun getItemCount(): Int = partnersList.size
}
