package com.naram.party_project.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.FriendDiffCallback
import com.naram.party_project.callback.Friend
import com.naram.party_project.callback.Party
import com.naram.party_project.databinding.ItemRequestedPartyBinding

class RequestedPartyListAdapter(
    val context: Context,
    val list: MutableList<Friend>,
    val itemClick: (Friend, Boolean) -> Unit
) : RecyclerView.Adapter<RequestedPartyListAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemRequestedPartyBinding,
        itemClick: (Friend, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Friend) {

            item.picture?.let {
                uploadImageFromCloud(it)
            }

            binding.tvRequestUserName.text = item.user_name

            binding.btnAcceptRequest.setOnClickListener {
                // TODO 서로의 친구 목록에 추가되도록
                itemClick(item, true)
            }

            binding.btnRefusalRequest.setOnClickListener {
                // TODO 요청 삭제
                itemClick(item, false)
            }

        }

        private fun uploadImageFromCloud(path: String) {
            val imagesRef = Firebase.storage.reference.child(path)

//            imagesRef.downloadUrl.addOnCompleteListener { task ->
//                task.addOnSuccessListener {
//                    Glide.with(itemView)
//                        .load(it)
//                        .placeholder(R.drawable.app_logo)
//                        .override(binding.ivPartyUserPicture.width, binding.ivPartyUserPicture.height)
//                        .into(binding.ivPartyUserPicture)
//                }.addOnFailureListener {
//                    Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                if(task.isComplete) Log.d("Adapter", "성공")
//            }

            val MAX_BYTE: Long = 400 * 400
            imagesRef.getBytes(MAX_BYTE).addOnSuccessListener {
                val options = BitmapFactory.Options();
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
                binding.ivRequestUserPicture.setImageBitmap(bitmap)
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
            ItemRequestedPartyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    fun setData(newData: List<Friend>) {
        Log.d("RequestPartyListAdapter", "setData")
        val diffResult = DiffUtil.calculateDiff(FriendDiffCallback(this.list, newData))

        this.list.run {
            list.clear()
            list.addAll(newData)
            diffResult.dispatchUpdatesTo(this@RequestedPartyListAdapter)
        }
    }

    fun notifyData(newData : List<Friend>) {
        Log.d("RequestPartyListAdapter", "notifyData")
        list.clear()
        list.addAll(newData)
        this.notifyDataSetChanged()
    }

}