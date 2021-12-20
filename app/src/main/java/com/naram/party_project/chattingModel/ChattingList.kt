package com.naram.party_project.chattingModel

data class ChattingList(
    val chatRoomUID: String,
    val myUID: String,
    val othersUID: String?,
    var receivedName: String?,
    var receivedPicture: String?,
    val lastMessage: String,
    val timeStamp: String
)