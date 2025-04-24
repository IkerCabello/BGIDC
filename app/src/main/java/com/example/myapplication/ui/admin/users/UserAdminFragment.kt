package com.example.myapplication.ui.admin.users

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dao.FirebaseCalls
import com.example.myapplication.databinding.FragmentUserAdminBinding
import com.example.myapplication.ui.model.User

class UserAdminFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchET: EditText
    private val firebaseCalls = FirebaseCalls()
    private lateinit var adapter: UsersAdapter
    private var allUsers: List<User> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseCalls.getAttendees(
            onSuccess = { attendees ->
                firebaseCalls.getSpeakers(
                    onSuccess = { speakers ->
                        val combinedList = attendees + speakers
                        setupRecyclerView(combinedList)
                    },
                    onFailure = { exception ->
                        Log.e("AdminFragment", "Error fetching speakers", exception)
                    }
                )
            },
            onFailure = { exception ->
                Log.e("AdminFragment", "Error fetching attendees", exception)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentUserAdminBinding.inflate(inflater, container, false)
        recyclerView = binding.usersRecyclerView
        searchET = binding.searchEditText

        return binding.root
    }

    private fun setupRecyclerView(users: List<User>) {
        allUsers = users
        adapter = UsersAdapter(users)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = adapter

        searchET.addTextChangedListener { text ->
            val filtered = allUsers.filter {
                it.name.contains(text.toString(), ignoreCase = true)
            }
            adapter.updateList(filtered)
        }
    }
}