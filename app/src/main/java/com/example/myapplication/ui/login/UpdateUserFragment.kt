package com.example.myapplication.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cloudinary.utils.ObjectUtils
import com.example.myapplication.R
import com.example.myapplication.dao.CloudinaryConfig
import com.example.myapplication.databinding.FragmentUpdateUserBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.edit

class UpdateUserFragment : Fragment() {

    private var _binding: FragmentUpdateUserBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            binding.imgProfile.setImageURI(imageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateUserBinding.inflate(inflater, container, false)

        val storedEmail = getStoredEmail()
        binding.etEmailEt.setText(storedEmail)

        val userTypeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.user_types,
            android.R.layout.simple_spinner_item
        )
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUType.adapter = userTypeAdapter

        binding.imgUpContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.btnUpdate.setOnClickListener {
            val newName = binding.etNameEt.text.toString()
            val newEmail = binding.etEmailEt.text.toString()
            val newPassword = binding.etPassEt.text.toString()
            val newCompany = binding.etCompanyEt.text.toString()
            val newPosition = binding.etPositionEt.text.toString()
            val newUserType = binding.spinnerUType.selectedItem.toString()
            val newAbout = binding.etAboutEt.text.toString()

            if (newName.isBlank() || newPassword.isBlank()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                uploadImageToCloudinary { imageUrl ->
                    if (imageUrl != null || imageUri == null) {
                        createUserInFirestore(
                            newName, newEmail, newPassword, newCompany, newPosition, newUserType, imageUrl ?: "", newAbout)
                    } else {
                        Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }

    private fun getStoredEmail(): String? {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        return sharedPref.getString("email", null)
    }

    private fun createUserInFirestore(name: String, email:String, password: String, company: String, position: String, usertype: String, profileImg: String, about: String) {
        val db = Firebase.firestore
        val baseId = name.lowercase().replace(" ", "")

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val newId: String
                val counter = result.size() + 1

                // Generate document name with format namesurname_n
                newId = "${baseId}_${counter}"

                // userId
                val userId = counter.toString()

                // Create the user with the user data
                val updatedUser = mapOf(
                    "id" to userId,
                    "name" to name,
                    "password" to password,
                    "company" to company,
                    "position" to position,
                    "user_type" to usertype,
                    "profile_img" to profileImg,
                    "sessions" to listOf<String>(),
                    "about" to about,
                    "email" to email,
                    "needsUpdate" to false,
                    "visible" to true
                )

                // Add user to firestore
                db.collection("users").document(newId)
                    .set(updatedUser)
                    .addOnSuccessListener {
                        // Clear SharedPrefs data
                        clearSharedPreferences()

                        // We save the last login
                        saveLoginData(userId)

                        Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT).show()
                        goToMain()
                    }
            }
    }

    private fun uploadImageToCloudinary(callback: (String?) -> Unit) {
        if (imageUri == null) {
            callback(null)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
                val file = File.createTempFile("upload", ".jpg", requireContext().cacheDir)
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

    private fun clearSharedPreferences() {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        sharedPref.edit {
            clear()  // Delete
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveLoginData(userId: String) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.apply {
            prefs.edit().putString("userId", userId)
            prefs.edit { putLong("lastLoginTime", System.currentTimeMillis()) }
            prefs.edit().putBoolean("isLoggedIn", true)
        }
    }

    private fun goToMain() {
        findNavController().navigate(R.id.action_update_userFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}