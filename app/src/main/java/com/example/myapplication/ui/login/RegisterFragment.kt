package com.example.myapplication.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.dao.sendEmail
import com.example.myapplication.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        //We call the function to send credentials

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmailet.text.toString().trim()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                val username = generateRandomUsername()
                val password = generateRandomPassword()

                sendEmail(email, username, password)
                saveCredentialsToSharedPreferences(email, username, password)
                goToLogin()

                Toast.makeText(context, "Credentials sent to your email", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Email address not valid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToLogin.setOnClickListener {

            val bundle = Bundle().apply {
                putBoolean("newLogin", false)
            }
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment, bundle)

        }

        return binding.root
    }

    private fun generateRandomUsername(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*"
        return (1..12)
            .map { chars.random() }
            .joinToString("")
    }

    private fun saveCredentialsToSharedPreferences(email: String, username: String, password: String) {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }

    private fun goToLogin() {

        val bundle = Bundle().apply {
            putBoolean("newLogin", true)
        }
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}