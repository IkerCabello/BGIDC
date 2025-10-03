package com.idvkm.bgidc.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.idvkm.bgidc.R
import com.idvkm.bgidc.dao.SmtpConfigStore
import com.idvkm.bgidc.dao.SmtpSender
import com.idvkm.bgidc.databinding.FragmentVerifyCodeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyCodeFragment : Fragment() {

    private var _binding: FragmentVerifyCodeBinding? = null
    private val binding get() = _binding!!

    private val changePasswordViewModel: ChangePasswordViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        changePasswordViewModel.email.observe(viewLifecycleOwner) { email ->
            binding.tvInstruction.text = if (!email.isNullOrBlank()) {
                "We have sent a code to your email: $email\nEnter the code in order to continue."
            } else {
                "Enter the code you received by email."
            }
        }

        binding.btnVerify.setOnClickListener {
            val entered = binding.codeEt.text?.toString()?.trim().orEmpty()
            if (entered.isEmpty()) {
                binding.codeTil.error = "Enter the code"
                return@setOnClickListener
            }
            binding.codeTil.error = null

            val expected = changePasswordViewModel.code.value
            if (expected == null) {
                Toast.makeText(requireContext(), "No code was generated. Please try again.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (entered == expected) {

                findNavController().navigate(R.id.action_verifyCodeFragment_to_changePasswordFragment)
            } else {
                binding.codeTil.error = "Incorrect code"
            }
        }

        binding.btnResend.setOnClickListener {

            val email = changePasswordViewModel.email.value
            if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newCode = generateCodeLocal()
            changePasswordViewModel.setCode(newCode)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val smtpUser = SmtpConfigStore.SMTP_USER
                    val smtpPass = SmtpConfigStore.SMTP_PASS
                    val smtpFrom = try {
                        SmtpConfigStore.SMTP_FROM_DEFAULT
                    } catch (e: Exception) {
                        "office@idvkm.com"
                    }
                    val sender = SmtpSender(requireContext(), smtpUser, smtpPass)
                    sender.sendChangePasswordEmail(from = smtpFrom, to = email, cCode = newCode)

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Code resented", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Error resenting: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun generateCodeLocal(): String {
        val chars = "0123456789"
        return (1..8).map { chars.random() }.joinToString("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}