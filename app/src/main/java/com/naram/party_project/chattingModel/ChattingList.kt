package com.naram.party_project.chattingModel

data class ChattingList(
    val chatRoomUID: String,
    val myUID: String,
    val othersUID: String?,
    val receivedName: String?,
    val receivedPicture: String?,
    var lastMessage: String,
    var timeStamp: String,
    var isRead: Boolean
)