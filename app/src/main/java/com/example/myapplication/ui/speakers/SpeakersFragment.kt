package com.example.myapplication.ui.speakers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dao.FirebaseCalls
import com.example.myapplication.databinding.FragmentSpeakersBinding
import com.example.myapplication.ui.model.User
import com.google.firebase.Timestamp

class SpeakersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var speakerAdapter: SpeakersAdapter
    private var speakersList = mutableListOf<User>()
    private val firebaseCalls = FirebaseCalls()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSpeakersBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewSpeakers

        speakersList = mutableListOf()

        firebaseCalls.getSpeakers(
            onSuccess = { speakers ->
                speakersList.clear()
                speakersList.addAll(speakers)
                speakerAdapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                Log.e("SpeakersFragment", "Error fetching speakers", exception)
            }
        )

        speakerAdapter = SpeakersAdapter(speakersList) { selectedUser ->
            openSpeakerDetailFragment(selectedUser)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = speakerAdapter

        return binding.root
    }

    private fun openSpeakerDetailFragment(user: User) {

        val sessionTitles = ArrayList(user.sessionDetails.map { it.title })
        val sessionStartTime = ArrayList(user.sessionDetails.map { it.start_time ?: Timestamp(0, 0) })
        val sessionEndTime = ArrayList(user.sessionDetails.map { it.end_time ?: Timestamp(0, 0) })
        val sessionRooms = ArrayList(user.sessionDetails.map { it.room })
        val sessionDescriptions = ArrayList(user.sessionDetails.map { it.description })

        val bundle = Bundle().apply {
            putString("id", user.id )
            putString("name", user.name)
            putString("company", user.company)
            putString("position", user.position)
            putString("about", user.about)
            putString("profileimg", user.profile_img)
            putString("linkedinUrl", user.linkedin_url)
            putStringArrayList("sessionTitles", sessionTitles)
            putParcelableArrayList("sessionStartTime", sessionStartTime)
            putParcelableArrayList("sessionEndTime", sessionEndTime)
            putStringArrayList("sessionRooms", sessionRooms)
            putStringArrayList("sessionDescriptions", sessionDescriptions)
        }

        findNavController().navigate(R.id.action_speakersFragment_to_speakerDetailFragment, bundle)
    }
}