package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.dao.FirebaseCalls
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.model.Session
import com.example.myapplication.ui.schedule.SessionsAdapter
import com.example.myapplication.ui.schedule.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var sessionsAdapter: SessionsAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val firebaseCalls = FirebaseCalls()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Show the bottom navigation menu
        (activity as? MainActivity)?.showBottomNavigationView()

        loadSessions()

        recyclerView = binding.rvHomeSessions
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        sessionsAdapter = SessionsAdapter(emptyList(), { selectedSession ->
            openSessionDetailFragment(selectedSession) //Open session in detail page
        }, { session ->
            //
        })
        recyclerView.adapter = sessionsAdapter

        // Observe sessions in the SharedViewModel
        sharedViewModel.sessionsList.observe(viewLifecycleOwner) { sessions ->
            sessionsAdapter.updateList(sessions)  // Update the RecyclerView with the sessions
        }

        // Navigate between pages
        binding.locationBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_locationFragment)
        }

        binding.attendeesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_attendeesFragment)
        }

        binding.speakersBtn.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_speakersFragment)
        }

        binding.partnersBtn.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_partnersFragment)
        }

        return root
    }

    // Load all the sessions
    private fun loadSessions() {
        firebaseCalls.getAllSessions(
            onSuccess = { sessions ->
                sharedViewModel.setSessions(sessions)
            },
            onFailure = { exception ->
                Log.e("AgendaFragment", "Error fetching sessions", exception)
            }
        )
    }

    //Open session in detail page passing the session data
    private fun openSessionDetailFragment(session: Session) {
        val bundle = Bundle().apply {
            putString("title", session.title)
            putString("room", session.room)
            putString("desc", session.description)
            putString("start_time", session.start_time?.toDate()?.toString() ?: "N/A")
            putString("end_time", session.end_time?.toDate()?.toString() ?: "N/A")
            putStringArrayList("speakerNames", ArrayList(session.speakerDetails.map { it.name ?: "Unknown" }))
            putStringArrayList("speakerPhotos", ArrayList(session.speakerDetails.map { it.profile_img ?: "" }))
            putStringArrayList("speakerCompanies", ArrayList(session.speakerDetails.map { it.company ?: "N/A" }))
            putStringArrayList("speakerPositions", ArrayList(session.speakerDetails.map { it.position ?: "N/A" }))
        }
        findNavController().navigate(R.id.action_homeFragment_to_sessionDetailFragment, bundle)
    }

}