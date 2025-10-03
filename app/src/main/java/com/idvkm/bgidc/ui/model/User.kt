package com.idvkm.bgidc.ui.model

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
    var linkedin_url: String,
    var sessions: List<DocumentReference> = listOf(),
    var sessionDetails: List<Session> = listOf(),
    val needsUpdate: Boolean,
    val visible: Boolean

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
        linkedin_url = "",
        sessions = emptyList(),
        sessionDetails = emptyList(),
        needsUpdate = false,
        visible = true
    )
}
