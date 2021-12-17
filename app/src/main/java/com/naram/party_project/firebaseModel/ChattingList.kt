package com.naram.party_project.firebaseModel

data class ChattingList(
    val chatRoomUID: String,
    val receivedName: String,
    val receivedPicture: String,
    val lastMessage: String,
    val timeStamp: String
)
