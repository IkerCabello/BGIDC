package com.example.myapplication.ui.admin.sessions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentListSessionsBinding
import com.example.myapplication.ui.model.Session
import com.google.firebase.firestore.FirebaseFirestore

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

        adapter = SessionAdapter(emptyList())
        binding.sessionRv.layoutManager = LinearLayoutManager(requireContext())
        binding.sessionRv.adapter = adapter

        loadSessions()

        binding.btnRoom1.setOnClickListener {
            filterSessionsByRoom("Room A1")
        }

        binding.btnRoom2.setOnClickListener {
            filterSessionsByRoom("Room B1")
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
        adapter.updateList(filtered)
    }

    private fun filterSessionsByQuery(query: String) {
        val filtered = allSessions.filter {
            it.title.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}