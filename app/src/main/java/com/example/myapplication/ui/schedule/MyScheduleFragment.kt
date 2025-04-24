package com.example.myapplication.ui.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.model.Session

class MyScheduleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnRoom1: Button
    private lateinit var btnRoom2: Button
    private lateinit var btnRoom3: Button
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var sessionsAdapter: SessionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_schedule, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMySchedule)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        btnRoom1 = view.findViewById(R.id.btn_room1)
        btnRoom2 = view.findViewById(R.id.btn_room2)
        btnRoom3 = view.findViewById(R.id.btn_room3)

        sessionsAdapter = SessionsAdapter(emptyList(), { selectedSession ->
            openSessionDetailFragment(selectedSession) //Open session in detail page
        }, { session ->
            //
        })
        recyclerView.adapter = sessionsAdapter

        sharedViewModel.selectedSessions.observe(viewLifecycleOwner) { sessions ->
            sessionsAdapter.updateList(sessions)
        }

        btnRoom1.setOnClickListener { filterSessions("Room A1") }
        btnRoom2.setOnClickListener { filterSessions("Room B1") }
        btnRoom3.setOnClickListener { showAllSessionsSorted() }

        return view
    }

    private fun filterSessions(room: String) {
        val filteredSessions = sharedViewModel.selectedSessions.value?.filter { it.room == room } ?: emptyList()
        sessionsAdapter.updateList(filteredSessions)
    }

    private fun showAllSessionsSorted() {
        val allSessions = sharedViewModel.selectedSessions.value ?: emptyList()
        val sortedSessions = allSessions.sortedBy { it.start_time?.toDate() }
        sessionsAdapter.updateList(sortedSessions)
    }

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
        findNavController().navigate(R.id.action_myScheduleFragment_to_sessionDetailFragment, bundle)
    }
}