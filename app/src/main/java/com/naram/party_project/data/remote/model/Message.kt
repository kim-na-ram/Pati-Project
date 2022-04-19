package com.naram.party_project.data.remote.model

data class Message(
    var uid: String,
    var name: String? = null,
    var picture: String? = null,
    var message: String,
    var timestamp: Any
)
