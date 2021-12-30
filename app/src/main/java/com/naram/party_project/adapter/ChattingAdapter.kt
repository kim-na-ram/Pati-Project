package com.naram.party_project.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.R
import com.naram.party_project.databinding.ItemChatBinding
import com.naram.party_project.chattingModel.Chatting
import com.naram.party_project.chattingModel.Message
import com.naram.party_project.mChattingDiffCallback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChattingAdapter(
    val myUID: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mDiffer = AsyncListDiffer(this, mChattingDiffCallback)

    val messageList = mutableListOf<Message>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder
    {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(
        val binding: ItemChatBinding
    ) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: Message)
        {

            if(item.uid == myUID)
            {
                binding.clSendContainer.visibility = View.VISIBLE
                binding.tvSendTime.text = getDateTime(item.timestamp)
                binding.tvSendMessage.text = item.message
            } else
            {
                binding.clReceivedContainer.visibility = View.VISIBLE
                binding.tvReceivedName.text = item.name
                binding.tvReceivedMessage.text = item.message
                binding.tvReceivedTime.text = getDateTime(item.timestamp)
                item.picture?.let {
                    uploadImageFromCloud(item.picture!!, itemView, binding)
                }
            }
        }
    }

//    use DiffUtil
//    override fun getItemCount() = mDiffer.currentList.size
//
//    override fun getItemId(position: Int) = mDiffer.currentList[position].hashCode().toLong()
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
//    {
//        val item = mDiffer.currentList[position]
//        (holder as ViewHolder).bind(item)
//    }

    override fun getItemCount() = messageList.size

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        val item = messageList[position]
        (holder as ViewHolder).bind(item)
    }

    private fun getDateTime(timestamp: Any): String
    {
        val date = Date(timestamp.toString().toLong())
        val today = Date()

        val dayFormat = SimpleDateFormat("yyyy.MM.dd")
        val timeFormat = SimpleDateFormat("a HH:mm")
        val allFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

        dayFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        timeFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        allFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        var result: String? = null

        result = if(dayFormat.format(date) == dayFormat.format(today)) {
            timeFormat.format(date)
        } else {
            allFormat.format(date)
        }

        return result!!
    }

    private fun uploadImageFromCloud(path: String, itemView: View, binding: ItemChatBinding)
    {
        val imagesRef = Firebase.storage.reference.child(path)

        imagesRef.downloadUrl.addOnCompleteListener { task ->
            task.addOnSuccessListener {
                Glide.with(itemView)
                    .load(it)
                    .placeholder(R.drawable.loading_image)
                    .into(binding.ivReceivedPicture)
            }.addOnFailureListener {
                Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun submitList(newList: MutableList<Message>)
    {
        if (mDiffer.currentList == newList) return
        else {
            Log.d("ChattingAdapter", "submitList")
            newList?.let {
                mDiffer.submitList(ArrayList<Message>(newList))
            }
        }
    }


}