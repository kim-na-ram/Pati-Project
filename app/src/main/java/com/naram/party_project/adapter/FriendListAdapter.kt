package com.naram.party_project.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.R
import com.naram.party_project.callback.Friend
import com.naram.party_project.databinding.ItemFriendsBinding
import com.naram.party_project.mFriendDiffCallback

class FriendListAdapter(
    val context: Context,
    val list: MutableList<Friend>,
    val itemClick: (Friend, Boolean) -> Unit
) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    private val mDiffer = AsyncListDiffer(this, mFriendDiffCallback)

    inner class ViewHolder(val binding: ItemFriendsBinding, itemClick: (Friend, Boolean) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Friend) {

            item.picture?.let {
                Log.d("FriendListAdapter", "${item.user_name} is ${item.bitmap}")
                if (item.bitmap == null)
                    uploadImageFromCloud(it)
                else binding.ivFriendsPicture.setImageBitmap(item.bitmap)
            }

            binding.tvFriendsName.text = item.user_name

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

            imagesRef.downloadUrl.addOnCompleteListener { task ->
                task.addOnSuccessListener {
                    Glide.with(itemView)
                        .load(it)
                        .placeholder(R.drawable.loading_image)
                        .override(binding.ivFriendsPicture.width, binding.ivFriendsPicture.height)
                        .into(binding.ivFriendsPicture)
                }.addOnFailureListener {
                    Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }

                if(task.isComplete) Log.d("Adapter", "성공")
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
        val item = mDiffer.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = mDiffer.currentList.size

    override fun getItemId(position: Int): Long {
        return mDiffer.currentList[position].hashCode().toLong()
    }

    fun submitList(newList: MutableList<Friend>) {
        if(mDiffer.currentList == newList) return
        else {
            Log.d("FriendListAdapter", "submitList")
            newList?.let {
                Log.d("FriendListAdapter", "newList : $newList")
                mDiffer.submitList(ArrayList<Friend>(newList))
            }
//            if (newList.isEmpty()) {
//                Log.d("FriendListAdapter", "newList : $newList")
//                mDiffer.submitList(ArrayList<Friend>(emptyList()))
//            }
        }

    }

    fun notifyData(newData: List<Friend>) {
        Log.d("FriendListAdapter", "notifyData")
        list.clear()
        list.addAll(newData)
        this.notifyDataSetChanged()
    }


}