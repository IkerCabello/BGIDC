package com.idvkm.bgidc.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cloudinary.utils.ObjectUtils
import com.idvkm.bgidc.R
import com.idvkm.bgidc.dao.SmtpConfigStore
import com.idvkm.bgidc.dao.SmtpSender
import com.idvkm.bgidc.databinding.FragmentEditProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.idvkm.bgidc.dao.CloudinaryConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private val changePasswordViewModel: ChangePasswordViewModel by activityViewModels()

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
            saveEditedProfile()
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
                    // PROTECCIÓN: si la vista ya fue destruida, salir
                    if (!isAdded || _binding == null) return@addOnSuccessListener

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
                    // PROTECCIÓN: si la vista ya fue destruida, salir
                    if (!isAdded || _binding == null) return@addOnFailureListener

                    Toast.makeText(requireContext(), "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "No user ID found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveEditedProfile() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id = sharedPref.getString("userId", null)

        if (id == null) {
            Toast.makeText(requireContext(), "No user ID found", Toast.LENGTH_SHORT).show()
            return
        }

        val newName = binding.cName.text.toString().trim()
        val newEmail = binding.cEmail.text.toString().trim()
        val newCompany = binding.cCompany.text.toString().trim()
        val newPosition = binding.cPosition.text.toString().trim()
        val newAbout = binding.cAbout.text.toString().trim()

        if (newName.isBlank()) {
            Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // PROTECCIÓN: si la vista ya fue destruida, salir
                if (!isAdded || _binding == null) return@addOnSuccessListener

                if (querySnapshot.isEmpty) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val doc = querySnapshot.documents[0]
                val docRef = doc.reference

                val currentPassword = doc.getString("password") ?: ""
                val currentUserType = doc.getString("user_type") ?: "attendee"
                val currentProfileImg = doc.getString("profile_img") ?: ""
                val currentSessions = doc.get("sessions") as? List<String> ?: listOf()

                fun updateUserInFirestore(profileImgUrl: String) {

                    if (!isAdded || _binding == null) return

                    val updatedUser = mapOf(
                        "id" to id,
                        "name" to newName,
                        "password" to currentPassword,
                        "company" to newCompany,
                        "position" to newPosition,
                        "user_type" to currentUserType,
                        "profile_img" to profileImgUrl,
                        "sessions" to currentSessions,
                        "about" to newAbout,
                        "email" to newEmail,
                        "needsUpdate" to false,
                        "visible" to true
                    )

                    docRef.update(updatedUser)
                        .addOnSuccessListener {
                            // PROTECCIÓN: comprobar de nuevo antes de usar binding/navegar
                            if (!isAdded || _binding == null) return@addOnSuccessListener

                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.navigation_profile)
                        }
                        .addOnFailureListener { e ->
                            if (!isAdded || _binding == null) return@addOnFailureListener
                            Toast.makeText(requireContext(), "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                if (imageUri != null) {
                    uploadImageToCloudinary { imageUrl ->
                        // callback ejecutado en Main thread (según implementación)
                        if (!isAdded || _binding == null) {
                            // si la vista ya no existe, no intentar update
                            return@uploadImageToCloudinary
                        }

                        if (imageUrl != null) {
                            updateUserInFirestore(imageUrl)
                        } else {
                            Toast.makeText(requireContext(), "Image upload failed, keeping current image", Toast.LENGTH_SHORT).show()
                            updateUserInFirestore(currentProfileImg)
                        }
                    }
                } else {
                    updateUserInFirestore(currentProfileImg)
                }
            }
            .addOnFailureListener { e ->
                if (!isAdded || _binding == null) return@addOnFailureListener
                Toast.makeText(requireContext(), "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    // PROTECCIÓN
                    if (!isAdded || _binding == null) return@addOnSuccessListener

                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id
                        val updateMap = hashMapOf<String, Any>(
                            "linkedin_url" to updatedLinkedin
                        )

                        db.collection("users").document(documentId)
                            .update(updateMap)
                            .addOnSuccessListener {
                                if (!isAdded || _binding == null) return@addOnSuccessListener
                                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.navigation_profile)
                            }
                            .addOnFailureListener {
                                if (!isAdded || _binding == null) return@addOnFailureListener
                                Toast.makeText(requireContext(), "Error updating", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }
    }

    private fun changePassword() {
        val email = binding.cEmail.text.toString()
        val code = generateCode()
        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getString("user_id", null)

        changePasswordViewModel.setCode(code)
        changePasswordViewModel.setEmail(email)
        changePasswordViewModel.setUserId(currentUserId.toString())

        lifecycleScope.launch {
            try {
                val smtpUser = SmtpConfigStore.SMTP_USER
                val smtpPass = SmtpConfigStore.SMTP_PASS
                val smtpFrom = try {
                    SmtpConfigStore.SMTP_FROM_DEFAULT
                } catch (e: Exception) {
                    "office@idvkm.com"
                }

                val sender = SmtpSender(
                    requireContext().toString(), smtpUser, smtpPass
                )
                sender.sendChangePasswordEmail(
                    from = smtpFrom,
                    to = email,
                    cCode = code
                )

                if (!isAdded || _binding == null) return@launch
                findNavController().navigate(R.id.action_editProfileFragment_to_verifyCodeFragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToCloudinary(callback: (String?) -> Unit) {
        // Capturamos el contexto y cacheDir en el hilo principal para evitar llamadas a requireContext() desde IO
        val ctx = if (isAdded) requireContext() else null
        if (ctx == null) {
            callback(null)
            return
        }

        if (imageUri == null) {
            callback(null)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = ctx.contentResolver.openInputStream(imageUri!!)
                val file = File.createTempFile("upload", ".jpg", ctx.cacheDir)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                val uploadResult = CloudinaryConfig.cloudinary.uploader().upload(
                    file,
                    ObjectUtils.asMap("folder", "profile_pictures")
                )

                val imageUrl = uploadResult["secure_url"] as String
                withContext(Dispatchers.Main) {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null)
                }
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