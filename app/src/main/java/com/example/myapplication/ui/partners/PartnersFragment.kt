package com.example.myapplication.ui.partners

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.model.Partner

class PartnersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PartnersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_partners, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPartners)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val partnersList = listOf(
            Partner("One Identity", R.drawable.oneidentity),
            Partner("IdStory", R.drawable.idstory),
            Partner("Ping Identity", R.drawable.pingidentity),
            Partner("Amatas", R.drawable.amatas),
            Partner("German-Bulgarian Chamber of Commerce and Industry", R.drawable.ahk),
            Partner("Dutch-Bulgarian Chamber of Commerce", R.drawable.ducc)
        )

        adapter = PartnersAdapter(partnersList)
        recyclerView.adapter = adapter

        return view

    }
}