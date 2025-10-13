package com.idvkm.bgidc.ui.admin.sessions

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.idvkm.bgidc.R
import androidx.navigation.fragment.findNavController
import com.idvkm.bgidc.ui.model.Session
import com.google.firebase.firestore.FirebaseFirestore
import com.idvkm.bgidc.databinding.FragmentListSessionsBinding

class ListSessionsFragment : Fragment() {

    private var _binding: FragmentListSessionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SessionAdapter
    private val allSessions = mutableListOf<Session>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SessionAdapter(
            emptyList(),
            onEditClick = { session -> openEditSession(session) },
            onDeleteClick = { session -> confirmDeleteSession(session) }
        )

        binding.sessionRv.layoutManager = LinearLayoutManager(requireContext())
        binding.sessionRv.adapter = adapter

        loadSessions()

        binding.btnRoom2.setOnClickListener {
            filterSessionsByRoom("Business Track")
        }

        binding.btnRoom3.setOnClickListener {
            filterSessionsByRoom("Tech Track")
        }

        binding.btnAll.setOnClickListener {
            adapter.updateList(allSessions)
        }

        binding.searchSessionEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterSessionsByQuery(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadSessions() {
        FirebaseFirestore.getInstance()
            .collection("sessions")
            .get()
            .addOnSuccessListener { result ->
                val sessions = result.toObjects(Session::class.java)
                    .sortedBy { it.start_time?.toDate() }

                allSessions.clear()
                allSessions.addAll(sessions)
                adapter.updateList(allSessions)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error loading sessions", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterSessionsByRoom(room: String) {
        val filtered = allSessions.filter { it.room == room }
        val sorted = filtered.sortedBy { it.start_time?.toDate() }
        adapter.updateList(sorted)
    }

    private fun filterSessionsByQuery(query: String) {
        val filtered = allSessions.filter {
            it.title.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }

    private fun openEditSession(session: Session) {
        val bundle = Bundle().apply {
            putInt("sessionId", session.sessionId)
        }
        findNavController().navigate(R.id.action_listSessionsFragment_to_editSessionFragment, bundle)
    }

    private fun confirmDeleteSession(session: Session) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Session")
            .setMessage("Are you sure you want to delete this session?")
            .setPositiveButton("Yes") { _, _ ->
                deleteSession(session)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteSession(session: Session) {
        val db = FirebaseFirestore.getInstance()
        db.collection("sessions")
            .whereEqualTo("sessionId", session.sessionId)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                if (doc != null) {
                    doc.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Session deleted", Toast.LENGTH_SHORT).show()
                            loadSessions() // Recarga la lista
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error deleting session", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Session not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error finding session", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}