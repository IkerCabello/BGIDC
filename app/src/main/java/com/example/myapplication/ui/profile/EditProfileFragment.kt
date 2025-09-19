package com.example.myapplication.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.dao.SmtpConfigStore
import com.example.myapplication.dao.SmtpSender
import com.example.myapplication.dao.sendPasswordResetEmail
import com.example.myapplication.databinding.FragmentEditProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadUserData()

        binding.cpassBtn.setOnClickListener {
            changePassword()
        }

        binding.saveBtn.setOnClickListener {
            saveLinkedinProfile()
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
                        val email = documents.getString("email")
                        val company = documents.getString("company")
                        val position = documents.getString("position")
                        val profileImgUrl = documents.getString("profile_img")
                        val about = documents.getString("about")
                        val linkedin = documents.getString("linkedin_url")

                        binding.cName.setText(name ?: "")
                        binding.cEmail.setText(email ?: "")
                        binding.cCompany.setText(company ?: "")
                        binding.cPosition.setText(position ?: "")
                        binding.cAbout.setText(about ?: "")
                        binding.cLinkedin.setText(linkedin ?: "")

                        if (!profileImgUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImgUrl)
                                .placeholder(R.drawable.userimage)
                                .into(binding.profileImg)
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

    private fun saveLinkedinProfile() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPref.getString("userId", null)
        val updatedLinkedin = binding.cLinkedin.text.toString().trim()

        if (id != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").whereEqualTo("id", id).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id
                        val updateMap = hashMapOf<String, Any>(
                            "linkedin_url" to updatedLinkedin
                        )

                        db.collection("users").document(documentId)
                            .update(updateMap)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.navigation_profile)
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }
    }

    private fun changePassword() {
        val email = binding.cEmail.text.toString()
        val code = generateCode()
        lifecycleScope.launch {
            try {
                val smtpUser = SmtpConfigStore.SMTP_USER
                val smtpPass = SmtpConfigStore.SMTP_PASS
                val smtpFrom = try {
                    SmtpConfigStore.SMTP_FROM_DEFAULT
                } catch (e: Exception) {
                    "office@idvkm.com"
                }

                val sender = SmtpSender(requireContext(), smtpUser, smtpPass
                )
                sender.sendChangePasswordEmail(
                    from = smtpFrom,
                    to = email,
                    cCode = code
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateCode(): String {
        val chars = "0123456789"
        return (1..8).map { chars.random() }.joinToString("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}