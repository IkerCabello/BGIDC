package com.example.myapplication.ui.speakers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentSpeakerDetailBinding
import com.example.myapplication.ui.model.Session
import com.google.firebase.Timestamp

class SpeakerDetailFragment : Fragment() {

    private lateinit var binding: FragmentSpeakerDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeakerDetailBinding.inflate(inflater, container, false)

        // Get user data
        arguments?.let { it ->
            val id = it.getString("id")
            val name = it.getString("name")
            val company = it.getString("company")
            val position = it.getString("position")
            val about = it.getString("about")
            val image = it.getString("profileimg")

            val sessionTitles = arguments?.getStringArrayList("sessionTitles") ?: arrayListOf()
            val sessionStartTime = it.getParcelableArrayList<Timestamp>("sessionStartTime") ?: arrayListOf()
            val sessionEndTime = it.getParcelableArrayList<Timestamp>("sessionEndTime") ?: arrayListOf()
            val sessionRooms = arguments?.getStringArrayList("sessionRooms") ?: arrayListOf()
            val sessionDescriptions = arguments?.getStringArrayList("sessionDescriptions") ?: arrayListOf()

            val sessions = sessionTitles.mapIndexed { index, title ->

                Session(
                    title = title,
                    description = sessionDescriptions.getOrNull(index) ?: "N/A",
                    room = sessionRooms.getOrNull(index) ?: "N/A",
                    start_time = sessionStartTime.getOrNull(index),
                    end_time = sessionEndTime.getOrNull(index),
                    sessionId = 0,
                )
            }

            binding.nameTv.text = name
            binding.pandcTv.text = "$position at $company"
            binding.descTv.text = about

            Glide.with(this)
                .load(image)
                .into(binding.profilePic)

            binding.spssRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.spssRecyclerView.adapter = SimpleSessionAdapter(sessions)

        }
        return binding.root
    }
}