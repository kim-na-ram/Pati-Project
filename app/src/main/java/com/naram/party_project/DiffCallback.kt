package com.naram.party_project

import androidx.recyclerview.widget.DiffUtil
import com.naram.party_project.callback.Friend
import com.naram.party_project.callback.Party

val mPartyDiffCallback = object : DiffUtil.ItemCallback<Party>() {
    override fun areItemsTheSame(oldItem: Party, newItem: Party): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: Party, newItem: Party): Boolean {
        return oldItem.email == newItem.email
    }
}

val mFriendDiffCallback = object : DiffUtil.ItemCallback<Friend>() {
    override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.email == newItem.email
    }
}