package com.example.myapplication.ui.admin.sessions

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.dao.FirebaseCalls
import com.example.myapplication.databinding.FragmentNewSessionBinding
import com.example.myapplication.ui.model.Session
import com.example.myapplication.ui.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class NewSessionFragment : Fragment() {

    private var _binding: FragmentNewSessionBinding? = null
    private val binding get() = _binding!!
    private val firebaseCalls = FirebaseCalls()
    private lateinit var speakersList: List<User>
    private val selectedSpeakers = mutableListOf<User>()
    private var selectedStartTime: Timestamp? = null
    private var selectedEndTime: Timestamp? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRoomDropdown()
        loadSpeakers()
        setupTimePickers()
        binding.btnCreateSession.setOnClickListener { validateAndCreateSession() }
    }

    private fun setupRoomDropdown() {
        val rooms = listOf("Room A1", "Room B1")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, rooms)
        binding.dropdownRoom.setAdapter(adapter)
    }

    private fun loadSpeakers() {
        firebaseCalls.getSpeakers(onSuccess = { speakers ->
            speakersList = speakers
            val names = speakers.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.multiSpeakerSelect.setAdapter(adapter)
            binding.multiSpeakerSelect.setTokenizer(android.widget.MultiAutoCompleteTextView.CommaTokenizer())

            binding.multiSpeakerSelect.setOnItemClickListener { _, _, position, _ ->
                val name = adapter.getItem(position)
                val user = speakersList.find { it.name == name }
                if (user != null) {
                    if (selectedSpeakers.contains(user)) {
                        selectedSpeakers.remove(user)
                    } else {
                        selectedSpeakers.add(user)
                    }
                }
            }
        }, onFailure = {
            Toast.makeText(context, "Error loading speakers", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setupTimePickers() {
        binding.etStartTime.setOnClickListener {
            showTimePicker { hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(2025, Calendar.OCTOBER, 10, hour, minute, 0)
                selectedStartTime = Timestamp(cal.time)
                binding.etStartTime.setText(formatTime(cal.time))
            }
        }

        binding.etEndTime.setOnClickListener {
            showTimePicker { hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(2025, Calendar.OCTOBER, 10, hour, minute, 0)
                selectedEndTime = Timestamp(cal.time)
                binding.etEndTime.setText(formatTime(cal.time))
            }
        }
    }

    private fun showTimePicker(onTimeSet: (Int, Int) -> Unit) {
        val cal = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hour, minute ->
            onTimeSet(hour, minute)
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun formatTime(date: Date): String {
        val formatter = SimpleDateFormat("HH:mm", Locale("en", "BG"))
        return formatter.format(date)
    }

    private fun validateAndCreateSession() {
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val room = binding.dropdownRoom.text.toString().trim()

        if (title.length < 6 || !title.matches(Regex("^[\\w\\s]+\$"))) {
            binding.etTitle.error = "Invalid title"
            return
        }

        if (desc.length < 30) {
            binding.etDescription.error = "The description must have more than 30 characters."
            return
        }

        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(context, "You must select the hours", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSpeakers.isEmpty()) {
            Toast.makeText(context, "Select at least one speaker", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseCalls.getAllSessions(onSuccess = { sessions ->
            val maxId = sessions.maxOfOrNull { it.sessionId } ?: 0
            val newId = maxId + 1

            val overlaps = sessions.any {
                it.room == room &&
                        it.start_time != null && it.end_time != null &&
                        selectedStartTime!!.toDate() < it.end_time.toDate() &&
                        selectedEndTime!!.toDate() > it.start_time.toDate()
            }

            if (overlaps) {
                Toast.makeText(context, "Time conflict in the room", Toast.LENGTH_LONG).show()
            } else {
                val docName = "session_${newId}"
                val session = Session(
                    title = title,
                    description = desc,
                    room = room,
                    start_time = selectedStartTime,
                    end_time = selectedEndTime,
                    sessionId = newId,
                    speakers = selectedSpeakers.map { FirebaseFirestore.getInstance().collection("users").document(
                        it.id
                    ) }
                )

                FirebaseFirestore.getInstance()
                    .collection("sessions")
                    .document(docName)
                    .set(session)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Session created", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error creating session", Toast.LENGTH_SHORT).show()
                    }
            }

            val docName = "session_$newId"
            val session = Session(
                title = title,
                description = desc,
                room = room,
                start_time = selectedStartTime,
                end_time = selectedEndTime,
                sessionId = newId,
                speakers = selectedSpeakers.map {
                    FirebaseFirestore.getInstance().collection("users").document(it.id)
                }
            )

            FirebaseFirestore.getInstance()
                .collection("sessions")
                .document(docName)
                .set(session)
                .addOnSuccessListener {
                    Toast.makeText(context, "Session created successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error saving session", Toast.LENGTH_SHORT).show()
                }
        }, onFailure = {
            Toast.makeText(context, "Error loading existing sessions", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}