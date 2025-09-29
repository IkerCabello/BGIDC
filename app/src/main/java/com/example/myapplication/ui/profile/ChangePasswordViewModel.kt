package com.example.myapplication.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangePasswordViewModel : ViewModel() {

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> = _email

    private val _code = MutableLiveData<String?>()
    val code: LiveData<String?> = _code

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    fun setEmail(e: String) { _email.value = e }
    fun setCode(c: String) { _code.value = c }
    fun setUserId(id: String) { _userId.value = id }

    // useful for clearing after flow finished
    fun clear() {
        _email.value = null
        _code.value = null
        _userId.value = null
    }
}