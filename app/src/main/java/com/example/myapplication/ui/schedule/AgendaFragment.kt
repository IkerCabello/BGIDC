package com.example.myapplication.ui.schedule

import android.os.Bundle
import android.util.Log
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
import com.example.myapplication.dao.FirebaseCalls
import com.example.myapplication.ui.model.Session
import java.text.SimpleDateFormat
import java.util.Locale

class AgendaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnRoom1: Button
    private lateinit var btnRoom2: Button
    private lateinit var btnRoom3: Button
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var sessionsAdapter: SessionsAdapter
    private val firebaseCalls = FirebaseCalls()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAgenda)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        btnRoom1 = view.findViewById(R.id.btn_room1)
        btnRoom2 = view.findViewById(R.id.btn_room2)
        btnRoom3 = view.findViewById(R.id.btn_room3)

        sessionsAdapter = SessionsAdapter(emptyList(), { selectedSession ->
            openSessionDetailFragment(selectedSession)
        }, { session ->
            sharedViewModel.addSession(session)
        })
        recyclerView.adapter = sessionsAdapter

        sharedViewModel.sessionsList.observe(viewLifecycleOwner) { sessions ->
            sessionsAdapter.updateList(sessions)
        }

        loadSessions()

        // Button actions for filtering
        btnRoom1.setOnClickListener {
            filterSessions("Room A1")
        }

        btnRoom2.setOnClickListener {
            filterSessions("Room B1")
        }

        btnRoom3.setOnClickListener {
            val allSessions = sharedViewModel.sessionsList.value ?: emptyList()
            val sortedSessions = allSessions.sortedBy { it.start_time?.toDate() } // Sorted by start_time
            sessionsAdapter.updateList(sortedSessions)  // Update list
        }

        return view
    }

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

    private fun filterSessions(room: String) {
        val filteredSessions = sharedViewModel.sessionsList.value?.filter { it.room == room } ?: emptyList()
        sessionsAdapter.updateList(filteredSessions)
    }

    private fun openSessionDetailFragment(session: Session) {

        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTime = session.start_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"
        val endTime = session.end_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"

        println("Session Title: ${session.title}")
        println("Speakers in session: ${session.speakerDetails}")

        session.speakerDetails.forEach { speaker ->
            println("Speaker: ${speaker.name}, Profile Image: ${speaker.profile_img}")
        }

        val speakerNames = ArrayList(session.speakerDetails.map { it.name ?: "Unknown" })
        val speakerPhotos = ArrayList(session.speakerDetails.map { it.profile_img ?: "" })
        val speakerCompanies = ArrayList(session.speakerDetails.map { it.company ?: "N/A" })
        val speakerPositions = ArrayList(session.speakerDetails.map { it.position ?: "N/A" })

        println("Speaker Photos List: $speakerPhotos")


        val bundle = Bundle().apply {
            putString("title", session.title)
            putString("time", "$startTime - $endTime")
            putString("room", session.room)
            putString("desc", session.description)
            putStringArrayList("speakerNames", speakerNames)
            putStringArrayList("speakerPhotos", speakerPhotos)
            putStringArrayList("speakerCompanies", speakerCompanies)
            putStringArrayList("speakerPositions", speakerPositions)
        }

        findNavController().navigate(R.id.action_agendaFragment_to_sessionDetailFragment, bundle)
    }
}