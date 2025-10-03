package com.idvkm.bgidc.ui.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Session(

    val title: String,
    val description: String,
    val room: String,
    val start_time: Timestamp?,
    val end_time: Timestamp?,
    val sessionId: Int,
    val speakers: List<DocumentReference> = listOf(),
    var speakerDetails: List<User> = listOf()

) {
    constructor() : this(
        title = "",
        description = "",
        room = "",
        start_time = null,
        end_time = null,
        sessionId = 0,
        speakers = emptyList(),
        speakerDetails = emptyList()
    )
}
