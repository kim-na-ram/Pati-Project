package com.naram.party_project.adapter

import android.content.Intent
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naram.party_project.ChattingActivity
import com.naram.party_project.ChattingListFragment
import com.naram.party_project.chattingModel.ChattingList

object ChattingListBindingAdapter {

    @BindingAdapter("items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: ArrayList<ChattingList>) {

        if (recyclerView.adapter == null) {
            val adapter = ChattingListAdapter {

            }
            adapter.setHasStableIds(true)
            recyclerView.adapter = adapter
        }

        val chattingListAdapter = recyclerView.adapter as ChattingListAdapter

        chattingListAdapter.chattingList = items
        chattingListAdapter.submitList()
    }

}