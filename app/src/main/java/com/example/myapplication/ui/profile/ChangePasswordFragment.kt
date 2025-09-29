package com.example.myapplication.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val changePasswordViewModel: ChangePasswordViewModel by activityViewModels()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnChange.setOnClickListener {
            val newPass = binding.newPassEt.text?.toString().orEmpty()
            val confirm = binding.confirmPassEt.text?.toString().orEmpty()

            val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getString("userId", null)

            if (newPass.length < 6) {
                binding.newPassTil.error = "The password must be at least 6 characters long"
                return@setOnClickListener
            } else {
                binding.newPassTil.error = null
            }

            if (newPass != confirm) {
                binding.confirmPassTil.error = "The passwords do not match."
                return@setOnClickListener
            } else {
                binding.confirmPassTil.error = null
            }

            if (userId.toString().isEmpty()) {
                Toast.makeText(requireContext(), "User not found.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            binding.btnChange.isEnabled = false

            if (userId != null) {
                db.collection("users").whereEqualTo("id", userId).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentId = querySnapshot.documents[0].id
                            val updateMap = hashMapOf<String, Any>(
                                "password" to newPass
                            )

                            db.collection("users").document(documentId)
                                .update(updateMap)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                                    changePasswordViewModel.clear()
                                    FirebaseAuth.getInstance().signOut()
                                    findNavController().navigate(R.id.action_ChangePasswordFragment_to_loginFragment)
                                }
                                .addOnFailureListener { e ->
                                    binding.btnChange.isEnabled = true
                                    e.printStackTrace()
                                    Toast.makeText(requireContext(), "The password could not be changed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_LONG).show()
                            binding.btnChange.isEnabled = true
                        }
                    }
                    .addOnFailureListener { e ->
                        binding.btnChange.isEnabled = true
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Error fetching user: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(requireContext(), "User ID is null", Toast.LENGTH_LONG).show()
                binding.btnChange.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}