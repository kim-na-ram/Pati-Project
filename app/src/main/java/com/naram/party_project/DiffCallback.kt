package com.naram.party_project

import androidx.recyclerview.widget.DiffUtil
import com.naram.party_project.callback.Friend
import com.naram.party_project.callback.Party

class PartyDiffCallback(
    private val oldData: List<Party>,
    private val newData: List<Party>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
    = oldData[oldItemPosition].email == newData[newItemPosition].email

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition] == newData[newItemPosition]
}

class FriendDiffCallback(
    private val oldData: List<Friend>,
    private val newData: List<Friend>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldData[oldItemPosition].email == newData[newItemPosition].email

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData[oldItemPosition] == newData[newItemPosition]
}