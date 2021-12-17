package com.naram.party_project

import androidx.recyclerview.widget.DiffUtil
import com.naram.party_project.callback.*
import com.naram.party_project.firebaseModel.ChatModel
import com.naram.party_project.firebaseModel.ChattingList

val mPartyFirebaseDiffCallback = object : DiffUtil.ItemCallback<PartyFirebase>() {
    override fun areItemsTheSame(oldItem: PartyFirebase, newItem: PartyFirebase): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: PartyFirebase, newItem: PartyFirebase): Boolean {
        return oldItem == newItem
    }
}

val mPartyDiffCallback = object : DiffUtil.ItemCallback<Party>() {
    override fun areItemsTheSame(oldItem: Party, newItem: Party): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: Party, newItem: Party): Boolean {
        return oldItem == newItem
    }
}

val mFriendFirebaseDiffCallback = object : DiffUtil.ItemCallback<FriendFirebase>() {
    override fun areItemsTheSame(oldItem: FriendFirebase, newItem: FriendFirebase): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: FriendFirebase, newItem: FriendFirebase): Boolean {
        return oldItem == newItem
    }
}

val mFriendDiffCallback = object : DiffUtil.ItemCallback<Friend>() {
    override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem == newItem
    }
}

val mChattingListDiffCallback = object : DiffUtil.ItemCallback<ChattingList>() {
    override fun areItemsTheSame(oldItem: ChattingList, newItem: ChattingList): Boolean {
        return oldItem.chatRoomUID == newItem.chatRoomUID
    }

    override fun areContentsTheSame(oldItem: ChattingList, newItem: ChattingList): Boolean {
        return oldItem == newItem
    }
}

val mChattingDiffCallback = object : DiffUtil.ItemCallback<ChatModel.Message>() {
    override fun areItemsTheSame(oldItem: ChatModel.Message, newItem: ChatModel.Message): Boolean {
        return (oldItem.uid == newItem.uid && oldItem.message == newItem.message)
    }

    override fun areContentsTheSame(oldItem: ChatModel.Message, newItem: ChatModel.Message): Boolean {
        return oldItem == newItem
    }
}