package com.example.myapplication.ui.attendees

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dao.FirebaseCalls
import com.example.myapplication.databinding.FragmentAttendeesBinding
import com.example.myapplication.ui.model.User

class AttendeesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var attendeesAdapter: AttendeesAdapter
    private var attendeesList = mutableListOf<User>()
    private val firebaseCalls = FirebaseCalls()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAttendeesBinding.inflate(inflater, container, false)
        recyclerView = binding.rvAttendees

        attendeesList = mutableListOf()

        // Receive attendees
        firebaseCalls.getAttendees(
            onSuccess = { attendees ->
                attendeesList.clear()
                attendeesList.addAll(attendees)
                attendeesAdapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                Log.e("AttendeesFragment", "Error fetching speakers", exception)
            }
        )

        attendeesAdapter = AttendeesAdapter(attendeesList) { selectedAttendee ->
            openAttendeeDetailFragment(selectedAttendee)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = attendeesAdapter

        return binding.root
    }

    // Open attendee in detail page passing data
    private fun openAttendeeDetailFragment(user: User) {
        val bundle = Bundle().apply {
            putString("name", user.name)
            putString("company", user.company)
            putString("position", user.position)
            putString("about", user.about)
            putString("profileimg", user.profile_img)
        }

        findNavController().navigate(R.id.action_attendeesFragment_to_attendeeDetailFragment, bundle)

    }
}