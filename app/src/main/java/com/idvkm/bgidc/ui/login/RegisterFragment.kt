package com.idvkm.bgidc.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.idvkm.bgidc.R
import com.idvkm.bgidc.dao.SmtpConfigStore
import com.idvkm.bgidc.dao.SmtpSender
import com.idvkm.bgidc.databinding.FragmentRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Disable back during registration flow (or implement behavior)
                }
            }
        )

        binding.tvGoToContact.visibility = View.GONE

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmailet.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Email address not valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Start the check flow
            lifecycleScope.launch {
                checkPendingOrExistingUser(email)
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("newLogin", false)
            }
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment, bundle)
        }

        binding.tvGoToContact.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_ContactFragment)
        }

        return root
    }

    private fun checkPendingOrExistingUser(email: String) {
        // 1) Check pending_users
        db.collection("pending_users").whereEqualTo("email", email).get()
            .addOnSuccessListener { pendingSnapshot ->
                if (!pendingSnapshot.isEmpty) {
                    // Email exists in pending_users -> send welcome email and navigate to UpdateUserFragment
                    sendWelcomeAndNavigate(email)
                } else {
                    // 2) Not in pending_users -> check users
                    db.collection("users").whereEqualTo("email", email).get()
                        .addOnSuccessListener { usersSnapshot ->
                            if (!usersSnapshot.isEmpty) {
                                // Email already registered
                                Toast.makeText(requireContext(), "This email is already registered.", Toast.LENGTH_LONG).show()
                            } else {
                                // Email not found anywhere
                                binding.tvError.visibility = View.VISIBLE
                                binding.tvGoToContact.visibility = View.VISIBLE
                            }
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                            Toast.makeText(requireContext(), "Error checking users: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error checking pending users: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendWelcomeAndNavigate(email: String) {
        // Send welcome email (coroutine to keep UI responsive)
        lifecycleScope.launch {
            try {
                val smtpUser = SmtpConfigStore.SMTP_USER
                val smtpPass = SmtpConfigStore.SMTP_PASS
                val smtpFrom = try {
                    SmtpConfigStore.SMTP_FROM_DEFAULT
                } catch (e: Exception) {
                    "office@idvkm.com"
                }

                val sender = SmtpSender(requireContext(), smtpUser.toString(), smtpPass.toString())

                // We removed temporary credentials generation, so pass empty strings (adjust if your SmtpSender requires other params)
                sender.sendWelcomeEmail(
                    from = smtpFrom,
                    to = email
                )

                // Navigate to UpdateUserFragment, passing the email so it can pre-fill the form
                val bundle = bundleOf("email" to email)
                findNavController().navigate(R.id.action_registerFragment_to_updateUserFragment, bundle)

                Toast.makeText(requireContext(), "Welcome email sent. Please complete your profile.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error sending welcome email: ${e.message}", Toast.LENGTH_LONG).show()
                // Still navigate to UpdateUserFragment, or decide to block navigation until email is sent
                val bundle = bundleOf("email" to email)
                findNavController().navigate(R.id.action_registerFragment_to_updateUserFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}