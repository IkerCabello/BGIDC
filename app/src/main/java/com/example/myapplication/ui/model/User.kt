package com.example.myapplication.ui.model

import com.google.firebase.firestore.DocumentReference

data class User (

    var id: String,
    val name: String,
    val email: String,
    val password: String,
    val company: String,
    val position: String,
    val user_type: String,
    val about: String,
    var profile_img: String,
    var sessions: List<DocumentReference> = listOf(),
    var sessionDetails: List<Session> = listOf(),
    val needsUpdate: Boolean

    ) {

    constructor() : this(
        id = "",
        name = "",
        email = "",
        password = "",
        company = "",
        position = "",
        user_type = "",
        about = "",
        profile_img = "",
        sessions = emptyList(),
        sessionDetails = emptyList(),
        needsUpdate = false
    )
}
