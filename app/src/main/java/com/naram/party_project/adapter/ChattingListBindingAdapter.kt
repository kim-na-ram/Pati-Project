package com.naram.party_project.adapter

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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
        Log.e("BindingAdapter", "$items")
        chattingListAdapter.submitList()
        chattingListAdapter.notifyDataSetChanged()
    }

}