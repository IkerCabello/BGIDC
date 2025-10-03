package com.idvkm.bgidc.ui.attendees

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
import com.idvkm.bgidc.R
import com.idvkm.bgidc.dao.FirebaseCalls
import com.idvkm.bgidc.databinding.FragmentAttendeesBinding
import com.idvkm.bgidc.ui.model.User

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
        firebaseCalls.getVisibleAttendees(
            onSuccess = { attendees ->
                attendeesList.clear()
                attendeesList.addAll(attendees)
                attendeesAdapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                Log.e("AttendeesFragment", "Error fetching attendees", exception)
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
            putString("id", user.id)
            putString("name", user.name)
            putString("company", user.company)
            putString("position", user.position)
            putString("about", user.about)
            putString("profileimg", user.profile_img)
            putString("linkedinUrl", user.linkedin_url)
        }

        findNavController().navigate(R.id.action_attendeesFragment_to_attendeeDetailFragment, bundle)

    }
}