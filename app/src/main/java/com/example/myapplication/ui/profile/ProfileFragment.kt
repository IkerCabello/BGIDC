package com.example.myapplication.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentProfileBinding
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.content.edit

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.adminPanelGroupLayout.visibility = View.GONE
        binding.separatorLine3.visibility = View.GONE

        setupButtons()
        loadUserData()

        return root
    }

    private fun setupButtons() {
        binding.settingsGroupLayout.setOnClickListener {
            goToSettings()
        }

        binding.editProfileGroupLayout.setOnClickListener {
            goToEdit()
        }

        binding.adminPanelGroupLayout.setOnClickListener {
            goToAdmin()
        }

        binding.btnLogOut.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnDeleteAcc.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun loadUserData() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("userId", null)

        if (userId != null) {
            firestore.collection("users")
                .whereEqualTo("id", userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.isEmpty) {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    } else {
                        val doc = document.documents[0]
                        val name = doc.getString("name")
                        val email = doc.getString("email")
                        val company = doc.getString("company")
                        val position = doc.getString("position")
                        val profileImgUrl = doc.getString("profile_img")

                        binding.usernameText.text = name ?: ""
                        binding.companyText.text = company ?: ""
                        binding.positionText.text = position ?: ""

                        if (!profileImgUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImgUrl)
                                .placeholder(R.drawable.userimage)
                                .into(binding.userImg)
                        }

                        if (name == "Admin" && email == "admin@gmail.com") {
                            binding.adminPanelGroupLayout.visibility = View.VISIBLE
                            binding.separatorLine3.visibility = View.VISIBLE
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "No user ID found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                logOutAndNavigate()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete this account?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logOutAndNavigate() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPref.edit { clear() }
        auth.signOut()
        findNavController().navigate(R.id.navigation_login)
    }

    private fun deleteAccount() {
        if (userId == null) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.documents[0].id
                    firestore.collection("users").document(docId)
                        .delete()
                        .addOnSuccessListener {
                            logOutAndNavigate()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to delete account: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Account not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToEdit() {
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
    }

    private fun goToAdmin() {
        findNavController().navigate(R.id.action_profileFragment_to_adminPanelFragment)
    }

    private fun goToSettings() {
        findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}