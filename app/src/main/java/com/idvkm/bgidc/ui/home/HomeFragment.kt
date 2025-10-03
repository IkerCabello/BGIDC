package com.idvkm.bgidc.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idvkm.bgidc.ui.schedule.SessionsAdapter
import com.idvkm.bgidc.MainActivity
import com.idvkm.bgidc.R
import com.idvkm.bgidc.dao.FirebaseCalls
import com.idvkm.bgidc.databinding.FragmentHomeBinding
import com.idvkm.bgidc.ui.model.Session

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var sessionsAdapter: SessionsAdapter
    private val firebaseCalls = FirebaseCalls()

    private var allSessions: List<Session> = emptyList()

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

        sessionsAdapter = SessionsAdapter(
            emptyList(),
            { selectedSession -> openSessionDetailFragment(selectedSession) }
        )
        recyclerView.adapter = sessionsAdapter

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
                allSessions = sessions.sortedBy { it.start_time?.toDate() }
                sessionsAdapter.updateList(allSessions)
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
            putStringArrayList("speakerNames", ArrayList(session.speakerDetails.map { it.name }))
            putStringArrayList("speakerPhotos", ArrayList(session.speakerDetails.map { it.profile_img }))
            putStringArrayList("speakerCompanies", ArrayList(session.speakerDetails.map { it.company }))
            putStringArrayList("speakerPositions", ArrayList(session.speakerDetails.map { it.position }))
        }
        findNavController().navigate(R.id.action_homeFragment_to_sessionDetailFragment, bundle)
    }

}