package com.example.myapplication.ui.admin.sessions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSessionAdminBinding

class SessionAdminFragment : Fragment() {

    private var _binding: FragmentSessionAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSessionAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.createSessionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_sessionAdminFragment_to_newSessionFragment)
        }

        binding.listSessionsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_sessionAdminFragment_to_listSessionsFragment)
        }

        return root
    }
}