package com.example.myapplication.ui.speakers

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import androidx.core.net.toUri

class SpeakerDetailFragment : Fragment() {

    private lateinit var binding: FragmentSpeakerDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeakerDetailBinding.inflate(inflater, container, false)

        arguments?.let { it ->
            val name = it.getString("name")
            val company = it.getString("company")
            val position = it.getString("position")
            val about = it.getString("about")
            val image = it.getString("profileimg")
            val linkedinUrl = it.getString("linkedinUrl")

            val sessionTitles = it.getStringArrayList("sessionTitles") ?: arrayListOf()
            val sessionStartTime = it.getParcelableArrayList<Timestamp>("sessionStartTime") ?: arrayListOf()
            val sessionEndTime = it.getParcelableArrayList<Timestamp>("sessionEndTime") ?: arrayListOf()
            val sessionRooms = it.getStringArrayList("sessionRooms") ?: arrayListOf()
            val sessionDescriptions = it.getStringArrayList("sessionDescriptions") ?: arrayListOf()

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