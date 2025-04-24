package com.example.myapplication.ui.attendees

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentAttendeeDetailBinding

class AttendeeDetailFragment: Fragment() {

    private lateinit var binding: FragmentAttendeeDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendeeDetailBinding.inflate(inflater, container, false)

        // Get user data
        arguments?.let {
            val name = it.getString("name")
            val company = it.getString("company")
            val position = it.getString("position")
            val about = it.getString("about")
            val image = it.getString("profileimg")

            binding.nameTv.text = name
            binding.pandcTv.text = "$position at $company"
            binding.descTv.text = about

            // Get user img
            Glide.with(this)
                .load(image)
                .into(binding.profilePic)
        }

        return binding.root
    }
}