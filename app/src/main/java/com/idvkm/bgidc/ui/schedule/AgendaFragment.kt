package com.idvkm.bgidc.ui.schedule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idvkm.bgidc.R
import com.idvkm.bgidc.dao.FirebaseCalls
import com.idvkm.bgidc.ui.model.Session
import com.idvkm.bgidc.ui.schedule.SessionsAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class AgendaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnRoom1: Button
    private lateinit var btnRoom2: Button
    private lateinit var btnRoom3: Button
    private lateinit var btnRoom4: Button
    private lateinit var sessionsAdapter: SessionsAdapter
    private val firebaseCalls = FirebaseCalls()

    private var allSessions: List<Session> = emptyList()

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
        btnRoom4 = view.findViewById(R.id.btn_room4)

        sessionsAdapter = SessionsAdapter(
            emptyList(),
            { selectedSession -> openSessionDetailFragment(selectedSession) }
        )
        recyclerView.adapter = sessionsAdapter

        loadSessions()

        btnRoom1.setOnClickListener { filterSessions("Panel 1") }
        btnRoom2.setOnClickListener { filterSessions("Panel 2") }
        btnRoom3.setOnClickListener { filterSessions("Panel 3") }
        btnRoom4.setOnClickListener { showAllSessions() }

        return view
    }

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

    private fun filterSessions(room: String) {
        val filtered = allSessions.filter { it.room == room }
        val sorted = filtered.sortedBy { it.start_time?.toDate() }
        sessionsAdapter.updateList(sorted)
    }

    private fun showAllSessions() {
        val sorted = allSessions.sortedBy { it.start_time?.toDate() }
        sessionsAdapter.updateList(sorted)
    }

    private fun openSessionDetailFragment(session: Session) {
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTime = session.start_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"
        val endTime = session.end_time?.toDate()?.let { timeFormatter.format(it) } ?: "N/A"

        val speakerNames = ArrayList(session.speakerDetails.map { it.name ?: "Unknown" })
        val speakerPhotos = ArrayList(session.speakerDetails.map { it.profile_img ?: "" })
        val speakerCompanies = ArrayList(session.speakerDetails.map { it.company ?: "N/A" })
        val speakerPositions = ArrayList(session.speakerDetails.map { it.position ?: "N/A" })

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