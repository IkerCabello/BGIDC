package com.example.myapplication.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentProfileBinding
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadUserData()

        binding.editProfileBtn.setOnClickListener {
            goToEdit()
        }

        return root
    }

    private fun loadUserData() {

        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPref.getString("userId", null)

        if (id != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.isEmpty) {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    } else {
                        val documents = document.documents[0]
                        val name = documents.getString("name")
                        val company = documents.getString("company")
                        val position = documents.getString("position")
                        val profileImgUrl = documents.getString("profile_img")

                        binding.usernameText.text = name ?: ""
                        binding.companyText.text = company ?: ""
                        binding.positionText.text = position ?: ""

                        if (!profileImgUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImgUrl)
                                .placeholder(R.drawable.userimage)
                                .into(binding.userImg)
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

    private fun goToEdit() {
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}