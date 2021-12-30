package com.naram.party_project.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naram.party_project.SearchPartyFragment
import com.naram.party_project.callback.PartyFirebase
import com.naram.party_project.callback.UserFirebase

object BindingAdapter {

    @BindingAdapter("items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: ArrayList<PartyFirebase>) {
        var user : UserFirebase? = null

        if (recyclerView.adapter == null) {
            val adapter = UserListAdapter() {
            }
            adapter.setHasStableIds(true)
            recyclerView.adapter = adapter
        }

        val userListAdapter = recyclerView.adapter as UserListAdapter

        userListAdapter.list = items
        userListAdapter.submitList()
    }

}