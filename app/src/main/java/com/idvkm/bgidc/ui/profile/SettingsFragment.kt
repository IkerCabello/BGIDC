package com.idvkm.bgidc.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.idvkm.bgidc.R
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var visibleSwitch: Switch
    private lateinit var btnSave: Button
    private val db = FirebaseFirestore.getInstance()
    private var userId: String? = null
    private var userType: String? = null
    private var currentVisibleValue: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        visibleSwitch = view.findViewById(R.id.visibleSwitch)
        btnSave = view.findViewById(R.id.btnSave)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = prefs.getString("userId", null)

        if (userId != null) {
            loadUserSettings(userId!!)
        } else {
            Toast.makeText(requireContext(), "Error: user not found", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            if (userType == "attendee" && userId != null) {
                val newVisibleValue = visibleSwitch.isChecked
                db.collection("users").document(userId!!)
                    .update("visible", newVisibleValue)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Configuration saved", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.navigation_profile)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error saving", Toast.LENGTH_SHORT).show()
                    }
            } else {

                findNavController().navigate(R.id.navigation_profile)
            }
        }

        return view
    }

    private fun loadUserSettings(userId: String) {
        db.collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val doc = document.documents[0]
                    userType = doc.getString("user_type")
                    val isVisible = doc.getBoolean("visible")

                    currentVisibleValue = isVisible == true

                    when (userType) {
                        "speaker", "admin" -> {
                            visibleSwitch.isChecked = true
                            visibleSwitch.isEnabled = false
                        }
                        "attendee" -> {
                            visibleSwitch.isChecked = currentVisibleValue
                            visibleSwitch.isEnabled = true
                        }
                        else -> {
                            visibleSwitch.isEnabled = false
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error loading user", Toast.LENGTH_SHORT).show()
            }
    }
}