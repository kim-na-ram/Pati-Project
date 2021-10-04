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
import com.naram.party_project.databinding.ItemFriendsBinding

class FriendListAdapter (
    val context: Context,
    val list: MutableList<Friend>,
    val itemClick: (Friend, Boolean) -> Unit
) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFriendsBinding, itemClick: (Friend, Boolean) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Friend) {

            item.picture?.let {
                uploadImageFromCloud(it)
            }

            binding.tvFriendsName.text = item.user_name

            Log.d("FriendListAdapter", "${item.user_name}")

            binding.btnSendMessage.setOnClickListener {
                // TODO 메세지 보내기
                itemClick(item, true)
            }

            binding.btnRemoveFriends.setOnClickListener {
                // TODO 친구 삭제
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
                binding.ivFriendsPicture.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFriendsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        Log.d("FriendListAdapter", "setData")
        val diffResult = DiffUtil.calculateDiff(FriendDiffCallback(this.list, newData))

        this.list.run {
            list.clear()
            list.addAll(newData)
            diffResult.dispatchUpdatesTo(this@FriendListAdapter)
        }
    }

    fun notifyData(newData : List<Friend>) {
        Log.d("FriendListAdapter", "notifyData")
        list.clear()
        list.addAll(newData)
        this.notifyDataSetChanged()
    }


}