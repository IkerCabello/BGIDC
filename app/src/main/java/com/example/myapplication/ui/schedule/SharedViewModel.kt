package com.example.myapplication.ui.schedule

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.ui.model.Session
import com.google.gson.Gson

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedSessions = MutableLiveData<MutableList<Session>>(mutableListOf())
    val selectedSessions: LiveData<MutableList<Session>> = _selectedSessions

    private val _sessionsList = MutableLiveData<List<Session>>()
    val sessionsList: LiveData<List<Session>> get() = _sessionsList

    fun addSession(session: Session) {
        val currentList = _selectedSessions.value.orEmpty().toMutableList()
        if (!currentList.contains(session)) {
            currentList.add(session)
            _selectedSessions.value = currentList
        }
    }

    fun setSessions(sessions: List<Session>) {
        _sessionsList.value = sessions
    }
}
