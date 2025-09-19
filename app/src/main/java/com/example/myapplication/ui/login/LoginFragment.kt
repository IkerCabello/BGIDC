package com.example.myapplication.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.dao.SmtpConfigStore
import com.example.myapplication.dao.SmtpSender
import com.example.myapplication.dao.sendSecurityEmail
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var failedAttempts = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            }
        )

        // Get if its the first login of the user
        val newLogin = arguments?.getBoolean("newLogin", false) ?: false

        val etEmail = view.findViewById<EditText>(R.id.etUseret)
        val etPassword = view.findViewById<EditText>(R.id.etPasswordet)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvRegister = view.findViewById<TextView>(R.id.tvGoToRegister)
        val check = view.findViewById<TextView>(R.id.checkTv)

        if(newLogin) {
            check.text = "Check your email for credentials"
            etEmail.hint = "Username"
        } else {
            check.text = "Enter your credentials"
            etEmail.hint = "E-mail address"
        }

        btnLogin.setOnClickListener {

            if(newLogin) {
                val user = etEmail.text.toString()
                val pass = etPassword.text.toString()
                loginNewUser(user, pass)
            } else {
                val email = etEmail.text.toString().trim()
                val pass = etPassword.text.toString().trim()
                loginUser(email, pass)
            }
        }

        tvRegister.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

        }

        return view
    }

    @SuppressLint("CommitPrefEdits")
    private fun loginNewUser(username: String, password: String) {
        val storedCredentials = getStoredCredentials()

        if (storedCredentials != null && storedCredentials.first == username && storedCredentials.second == password) {

            findNavController().navigate(R.id.action_loginFragment_to_update_userFragment)

        } else {
            Toast.makeText(context, "Incorrect credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getStoredCredentials(): Pair<String?, String>? {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)
        val password = sharedPref.getString("password", null)
        return if (username != null && password != null) {
            Pair(username, password)
        } else {
            null
        }
    }

    private fun loginUser (email: String, pass: String) {
        val db = FirebaseFirestore.getInstance()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                } else {
                    val document = documents.documents[0]
                    val dbPassword = document.getString("password")
                    val userId = document.getString("id")

                    if (dbPassword == pass) {

                        failedAttempts = 0

                        // Save data in SharedPreferences
                        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("userId", userId)
                            putLong("lastLoginTime", System.currentTimeMillis())
                            putBoolean("isLoggedIn", true)
                            apply()
                        }

                        val name = document.getString("name") ?: ""

                        Toast.makeText(requireContext(), "Logged in successfully", Toast.LENGTH_SHORT).show()

                        if (name.equals("Admin", ignoreCase = true)) {
                            findNavController().navigate(R.id.action_loginFragment_to_adminPanelFragment)
                        } else {
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                    } else {
                        failedAttempts++
                        Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()

                        // Send security email with 3 failed attempts
                        if (failedAttempts >= 3) {
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
                                    sender.sendWarningEmail(
                                        from = smtpFrom,
                                        to = email
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}