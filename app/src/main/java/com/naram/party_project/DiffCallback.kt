package com.naram.party_project

import androidx.recyclerview.widget.DiffUtil
import com.naram.party_project.callback.Friend
import com.naram.party_project.callback.FriendFirebase
import com.naram.party_project.callback.Party
import com.naram.party_project.callback.PartyFirebase

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