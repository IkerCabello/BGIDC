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
import com.example.myapplication.databinding.FragmentEditSessionBinding
import com.example.myapplication.ui.model.Session
import com.example.myapplication.ui.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

class EditSessionFragment : Fragment() {

    private var _binding: FragmentEditSessionBinding? = null
    private val binding get() = _binding!!

    private var sessionId: Int = -1
    private var selectedStartTime: Timestamp? = null
    private var selectedEndTime: Timestamp? = null
    private lateinit var allSpeakers: List<User>
    private val selectedSpeakers = mutableListOf<User>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionId = arguments?.getInt("sessionId") ?: -1
        if (sessionId == -1) {
            Toast.makeText(requireContext(), "Invalid session", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        setupRoomDropdown()
        setupTimePickers()
        loadSpeakers()
        loadSessionData()

        binding.btnUpdateSession.setOnClickListener {
            validateAndSave()
        }
    }

    private fun setupRoomDropdown() {
        val rooms = listOf("Room A1", "Room B1")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rooms)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editRoomSpinner.adapter = adapter
    }

    private fun setupTimePickers() {
        binding.editStartTime.setOnClickListener {
            showTimePicker { hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(2025, Calendar.OCTOBER, 10, hour, minute, 0)
                selectedStartTime = Timestamp(cal.time)
                binding.editStartTime.setText(formatTime(cal.time))
            }
        }

        binding.editEndTime.setOnClickListener {
            showTimePicker { hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(2025, Calendar.OCTOBER, 10, hour, minute, 0)
                selectedEndTime = Timestamp(cal.time)
                binding.editEndTime.setText(formatTime(cal.time))
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

    private fun loadSpeakers() {
        FirebaseCalls().getSpeakers(onSuccess = { speakers ->
            allSpeakers = speakers
            val names = speakers.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.editMultiSpeakerSelect.setAdapter(adapter)
            binding.editMultiSpeakerSelect.setTokenizer(android.widget.MultiAutoCompleteTextView.CommaTokenizer())

            binding.editMultiSpeakerSelect.setOnItemClickListener { _, _, position, _ ->
                val name = adapter.getItem(position)
                val user = allSpeakers.find { it.name == name }
                if (user != null && user.id != null && selectedSpeakers.none { it.id == user.id }) {
                    selectedSpeakers.add(user)
                }
            }
        }, onFailure = {
            Toast.makeText(requireContext(), "Error loading speakers", Toast.LENGTH_SHORT).show()
        })
    }

    private fun loadSessionData() {
        db.collection("sessions")
            .whereEqualTo("sessionId", sessionId)
            .limit(1)
            .get()
            .addOnSuccessListener { docs ->
                val doc = docs.documents.firstOrNull()
                if (doc != null) {
                    val session = doc.toObject(Session::class.java)
                    if (session != null) {
                        binding.editTitle.setText(session.title)
                        binding.editDescription.setText(session.description)
                        binding.editRoomSpinner.setSelection(if (session.room == "Room A1") 0 else 1)

                        session.start_time?.let {
                            selectedStartTime = it
                            binding.editStartTime.setText(formatTime(it.toDate()))
                        }
                        session.end_time?.let {
                            selectedEndTime = it
                            binding.editEndTime.setText(formatTime(it.toDate()))
                        }

                        session.speakers?.forEach { ref ->
                            ref.get().addOnSuccessListener { userDoc ->
                                val user = userDoc.toObject(User::class.java)?.copy(id = userDoc.id)
                                if (user != null && selectedSpeakers.none { it.id == user.id }) {
                                    selectedSpeakers.add(user)
                                    binding.editMultiSpeakerSelect.append("${user.name}, ")
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error loading session", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateAndSave() {
        val title = binding.editTitle.text.toString().trim()
        val desc = binding.editDescription.text.toString().trim()
        val room = binding.editRoomSpinner.selectedItem.toString()

        if (title.length < 6 || !title.matches(Regex("^[\\w\\s]+$"))) {
            binding.editTitle.error = "Invalid title"
            return
        }

        if (desc.length < 30) {
            binding.editDescription.error = "Description too short"
            return
        }

        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(requireContext(), "Select valid start and end times", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSpeakers.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one speaker", Toast.LENGTH_SHORT).show()
            return
        }

        selectedSpeakers.forEach {
            Log.d("SpeakerCheck", "Saving speaker: ${it.name}, ID: ${it.id}")
        }

        db.collection("sessions")
            .whereEqualTo("sessionId", sessionId)
            .limit(1)
            .get()
            .addOnSuccessListener { docs ->
                val doc = docs.documents.firstOrNull()
                if (doc != null) {
                    val speakerRefs = selectedSpeakers.mapNotNull {
                        it.id?.let { id -> db.collection("users").document(id) }
                    }

                    val session = Session(
                        title = title,
                        description = desc,
                        room = room,
                        start_time = selectedStartTime,
                        end_time = selectedEndTime,
                        sessionId = sessionId,
                        speakers = speakerRefs
                    )

                    doc.reference.set(session)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Session updated", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error updating session", Toast.LENGTH_SHORT).show()
                        }
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