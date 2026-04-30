package com.example.madlabca2.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Participant(
    val id: String? = null,
    val participantName: String? = null,
    val email: String? = null,
    val collegeName: String? = null,
    val mobileNumber: String? = null,
    val eventName: String? = null,
    val teamSize: Int? = null
)
