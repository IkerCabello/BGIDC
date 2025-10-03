package com.idvkm.bgidc.dao

import android.util.Log
import com.idvkm.bgidc.ui.model.Session
import com.idvkm.bgidc.ui.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCalls {

    // All the calls to the database

    private val db = FirebaseFirestore.getInstance()

    fun getSpeakers(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .whereEqualTo("user_type", "speaker")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val speakersList = mutableListOf<User>()
                val totalSpeakers = querySnapshot.size()
                var processedSpeakers = 0

                if (totalSpeakers == 0) {
                    onSuccess(speakersList)
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    val speaker = document.toObject(User::class.java)
                    speaker?.id = document.id

                    val profileimgUrl = document.getString("profile_img")
                    if (profileimgUrl != null) {
                        speaker?.profile_img = profileimgUrl
                    }

                    val sessionRefs = document.get("sessions") as? List<DocumentReference> ?: listOf()
                    speaker?.sessions = sessionRefs

                    if (speaker != null) {
                        fetchSessionDetails(sessionRefs) { sessionList ->
                            speaker.sessionDetails = sessionList
                            speakersList.add(speaker)
                            processedSpeakers++

                            if (processedSpeakers == totalSpeakers) {
                                onSuccess(speakersList)
                            }
                        }
                    } else {
                        processedSpeakers++
                        if (processedSpeakers == totalSpeakers) {
                            onSuccess(speakersList)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getAttendees(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .whereEqualTo("user_type", "attendee")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val attendeesList = mutableListOf<User>()
                for (document in querySnapshot.documents) {
                    val attendee = document.toObject(User::class.java)
                    attendee?.id = document.id

                    val profileimgUrl = document.getString("profile_img")

                    if (profileimgUrl != null) {
                        attendee?.profile_img = profileimgUrl
                    }

                    if (attendee != null) {
                        attendeesList.add(attendee)
                    }
                }
                onSuccess(attendeesList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getVisibleAttendees(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .whereEqualTo("user_type", "attendee")
            .whereEqualTo("visible", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val attendeesList = mutableListOf<User>()
                for (document in querySnapshot.documents) {
                    val attendee = document.toObject(User::class.java)
                    attendee?.id = document.id

                    val profileimgUrl = document.getString("profile_img")

                    if (profileimgUrl != null) {
                        attendee?.profile_img = profileimgUrl
                    }

                    if (attendee != null) {
                        attendeesList.add(attendee)
                    }
                }
                onSuccess(attendeesList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteUser(userId: String, onComplete: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error deleting user", e)
            }
    }

    fun getAllSessions(onSuccess: (List<Session>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("sessions").get()
            .addOnSuccessListener { result ->
                val sessions = mutableListOf<Session>()
                val sessionFetchCount = result.size()
                var completedFetches = 0

                for (document in result) {
                    val session = document.toObject(Session::class.java)
                    fetchSpeakersDetails(session.speakers) { speakerList ->
                        session.speakerDetails = speakerList
                        sessions.add(session)
                        completedFetches++

                        if (completedFetches == sessionFetchCount) {

                            val sortedSessions = sessions.sortedBy { it.start_time?.toDate() }
                            onSuccess(sortedSessions)
                        }
                    }
                }

                if (result.isEmpty) {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun fetchSpeakersDetails(speakerRefs: List<DocumentReference>, onComplete: (List<User>) -> Unit) {
        val speakersList = mutableListOf<User>()
        val totalSpeakers = speakerRefs.size
        var fetchedSpeakers = 0

        for (speakerRef in speakerRefs) {
            speakerRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {

                        val speaker = document.toObject(User::class.java)
                        if (speaker != null) {
                            speakersList.add(speaker)
                        }
                    }
                    fetchedSpeakers++
                    if (fetchedSpeakers == totalSpeakers) {
                        onComplete(speakersList)
                    }
                }
                .addOnFailureListener {
                    fetchedSpeakers++
                    if (fetchedSpeakers == totalSpeakers) {
                        onComplete(speakersList)
                    }
                }
        }
    }

    private fun fetchSessionDetails(sessionRefs: List<DocumentReference>, onComplete: (List<Session>) -> Unit) {
        val sessionsList = mutableListOf<Session>()
        val totalSessions = sessionRefs.size
        var fetchedSessions = 0

        if (totalSessions == 0) {
            onComplete(sessionsList)
            return
        }

        for (sessionRef in sessionRefs) {
            sessionRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val session = document.toObject(Session::class.java)
                        if (session != null) {
                            sessionsList.add(session)
                        }
                    }
                    fetchedSessions++
                    if (fetchedSessions == totalSessions) {
                        onComplete(sessionsList)
                    }
                }
                .addOnFailureListener {
                    fetchedSessions++
                    if (fetchedSessions == totalSessions) {
                        onComplete(sessionsList)
                    }
                }
        }
    }
}