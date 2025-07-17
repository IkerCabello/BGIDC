package com.example.myapplication.ui.attendees

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentAttendeeDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class AttendeeDetailFragment : Fragment() {

    private lateinit var binding: FragmentAttendeeDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendeeDetailBinding.inflate(inflater, container, false)

        val sharedPrefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val currentEmail = sharedPrefs.getString("userEmail", null)
        val currentName = sharedPrefs.getString("userName", null)

        arguments?.let {
            val id = it.getString("id") // user ID
            val name = it.getString("name")
            val company = it.getString("company")
            val position = it.getString("position")
            val about = it.getString("about")
            val image = it.getString("profileimg")
            val linkedinUrl = it.getString("linkedinUrl")

            binding.nameTv.text = name
            binding.pandcTv.text = "$position at $company"
            binding.descTv.text = about

            Glide.with(this)
                .load(image)
                .into(binding.profilePic)

            if (!linkedinUrl.isNullOrEmpty()) {
                binding.linkedinBtn.visibility = View.VISIBLE
                binding.linkedinBtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, linkedinUrl.toUri())
                    startActivity(intent)
                }
            } else {
                binding.linkedinBtn.visibility = View.GONE
            }

            // Show delete button if admin
            if (currentEmail == "admin@gmail.com" && currentName == "Admin") {
                binding.deleteBtn2.visibility = View.VISIBLE
                binding.deleteBtn2.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes") { _, _ ->
                            if (!id.isNullOrEmpty()) {
                                FirebaseFirestore.getInstance().collection("users").document(id)
                                    .delete()
                                    .addOnSuccessListener {
                                        findNavController().popBackStack() // o usar navigate(R.id.attendeesFragment) si lo tienes definido
                                    }
                            }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            } else {
                binding.deleteBtn2.visibility = View.GONE
            }
        }

        return binding.root
    }
}