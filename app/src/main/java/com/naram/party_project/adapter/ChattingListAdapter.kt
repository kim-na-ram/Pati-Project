package com.naram.party_project.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.*
import com.naram.party_project.databinding.ItemChattingListBinding
import com.naram.party_project.chattingModel.ChattingList
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING
import java.text.SimpleDateFormat
import java.util.*

class ChattingListAdapter(
    val itemClick: (ChattingList) -> Unit
) : RecyclerView.Adapter<ChattingListAdapter.ViewHolder>() {

    var chattingList = mutableListOf<ChattingList>()

    private val differ = AsyncListDiffer(this, mChattingListDiffCallback)

        inner class ViewHolder(val binding: ItemChattingListBinding, itemClick: (ChattingList) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChattingList) {

            binding.chatting = item

            Log.e("Adapter", "$item")

            if(!item.isRead) {
                binding.tvNewMessage.visibility = View.VISIBLE
            } else {
                binding.tvNewMessage.visibility = View.GONE
            }

            item.receivedPicture?.let {
                uploadImageFromCloud(it, itemView, binding)
            }

//            binding.tvLastMessage.text = item.lastMessage
            binding.tvLastMessageTime.text = getDateTime(item.timeStamp)

            itemView.setOnClickListener {
                itemClick(item)
            }

        }

    }

    private fun getDateTime(timestamp: Any): String {
        val date = Date(timestamp.toString().toLong())
        val today = Date()

        val dayFormat = SimpleDateFormat("yyyy.MM.dd")
        val timeFormat = SimpleDateFormat("a HH:mm")
        val allFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

        dayFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        timeFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        allFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        var result: String? = null

        result = if (dayFormat.format(date) == dayFormat.format(today)) {
            timeFormat.format(date)
        } else {
            allFormat.format(date)
        }

        return result!!
    }

    private fun uploadImageFromCloud(
        path: String,
        itemView: View,
        binding: ItemChattingListBinding
    ) {
        val imagesRef = Firebase.storage.reference.child(path)

        imagesRef.downloadUrl.addOnCompleteListener { task ->
            task.addOnSuccessListener {
                Glide.with(itemView)
                    .load(it)
                    .placeholder(R.drawable.loading_image)
                    .into(binding.ivReceivedPicture)
            }.addOnFailureListener {
                Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemChattingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun submitList() {
        Log.d("Adapter", "submitList")
        differ.submitList(chattingList)
    }

}