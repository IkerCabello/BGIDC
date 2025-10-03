package com.idvkm.bgidc.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.idvkm.bgidc.R
import com.idvkm.bgidc.databinding.FragmentAdminPanelBinding

class AdminPanelFragment : Fragment() {

    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.sessionsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_adminPanelFragment_to_sessionAdminFragment)
        }

        binding.usersBtn.setOnClickListener {
            findNavController().navigate(R.id.action_adminPanelFragment_to_sessionUserFragment)
        }

        binding.exitBtn.setOnClickListener {
            findNavController().navigate(R.id.action_adminPanelFragment_to_homeFragment)
        }

        return root
    }
}