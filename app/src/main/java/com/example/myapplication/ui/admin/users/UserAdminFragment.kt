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
import com.example.myapplication.dao.FirebaseCalls
import com.example.myapplication.databinding.FragmentUserAdminBinding
import com.example.myapplication.ui.model.User

class UserAdminFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchET: EditText
    private val firebaseCalls = FirebaseCalls()
    private lateinit var adapter: UsersAdapter
    private var allUsers: List<User> = emptyList()
    private lateinit var binding: FragmentUserAdminBinding

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

        binding.filterAllBtn.setOnClickListener {
            adapter.updateList(allUsers)
        }

        binding.filterSpeakersBtn.setOnClickListener {
            val filtered = allUsers.filter { it.user_type.equals("speaker", ignoreCase = true) }
            adapter.updateList(filtered)
        }

        binding.filterAttendeesBtn.setOnClickListener {
            val filtered = allUsers.filter { it.user_type.equals("attendee", ignoreCase = true) }
            adapter.updateList(filtered)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserAdminBinding.inflate(inflater, container, false)
        recyclerView = binding.usersRecyclerView
        searchET = binding.searchEditText

        return binding.root
    }

    private fun setupRecyclerView(users: List<User>) {
        allUsers = users

        adapter = UsersAdapter(users) { userToDelete ->
            showDeleteConfirmationDialog(userToDelete)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = adapter

        searchET.addTextChangedListener { text ->
            val filtered = allUsers.filter {
                it.name.contains(text.toString(), ignoreCase = true)
            }
            adapter.updateList(filtered)
        }
    }

    private fun showDeleteConfirmationDialog(user: User) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete user")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Yes") { _, _ ->
                firebaseCalls.deleteUser(user.id) {
                    reloadUsers()
                }
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()
    }

    private fun reloadUsers() {
        firebaseCalls.getAttendees(
            onSuccess = { attendees ->
                firebaseCalls.getSpeakers(
                    onSuccess = { speakers ->
                        val combinedList = attendees + speakers
                        adapter.updateList(combinedList)
                        allUsers = combinedList
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
}