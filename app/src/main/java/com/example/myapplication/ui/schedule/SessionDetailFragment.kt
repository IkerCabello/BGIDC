package com.example.myapplication.ui.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentSessionDetailBinding
import com.example.myapplication.ui.model.User

class SessionDetailFragment : Fragment() {

    private var _binding: FragmentSessionDetailBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val title = it.getString("title")
            val time = it.getString("time")
            val room = it.getString("room")
            val desc = it.getString("desc")

            val speakerNames = it.getStringArrayList("speakerNames") ?: arrayListOf()
            val speakerPhotos = it.getStringArrayList("speakerPhotos") ?: arrayListOf()
            val speakerCompanies = it.getStringArrayList("speakerCompanies") ?: arrayListOf()
            val speakerPositions = it.getStringArrayList("speakerPositions") ?: arrayListOf()

            val speakersList = speakerNames.mapIndexed { index, name ->
                User(
                    id = "",
                    name = name,
                    email = "",
                    password = "",
                    company = speakerCompanies.getOrNull(index) ?: "N/A",
                    position = speakerPositions.getOrNull(index) ?: "N/A",
                    user_type = "",
                    about = "",
                    profile_img = speakerPhotos.getOrNull(index) ?: "",
                    needsUpdate = false
                )
            }

            binding.tvsTitle.text = title
            binding.tvsTime.text = time
            binding.tvsRoom.text = room
            binding.tvsDesc.text = desc

            binding.rvSpeakersInSession.layoutManager = LinearLayoutManager(requireContext())
            binding.rvSpeakersInSession.adapter = SimpleSpeakersAdapter(speakersList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}