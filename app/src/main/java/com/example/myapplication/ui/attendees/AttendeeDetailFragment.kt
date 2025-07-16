package com.example.myapplication.ui.attendees

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentAttendeeDetailBinding
import androidx.core.net.toUri

class AttendeeDetailFragment : Fragment() {

    private lateinit var binding: FragmentAttendeeDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendeeDetailBinding.inflate(inflater, container, false)

        arguments?.let {
            val name = it.getString("name")
            val company = it.getString("company")
            val position = it.getString("position")
            val about = it.getString("about")
            val image = it.getString("profileimg")
            val linkedinUrl = it.getString("linkedinUrl")

            binding.nameTv.text = name
            binding.pandcTv.text = "$position at $company"
            binding.descTv.text = about

            Glide.with(this)
                .load(image)
                .into(binding.profilePic)

            if (!linkedinUrl.isNullOrEmpty()) {
                binding.linkedinBtn.visibility = View.VISIBLE
                binding.linkedinBtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, linkedinUrl.toUri())
                    startActivity(intent)
                }
            } else {
                binding.linkedinBtn.visibility = View.GONE
            }
        }

        return binding.root
    }
}