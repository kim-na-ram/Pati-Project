package com.naram.party_project.ui.main.friend

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
import com.naram.party_project.data.remote.model.FriendFirebase

import com.naram.party_project.databinding.ItemRequestedPartyBinding
import com.naram.party_project.mFriendFirebaseDiffCallback

//class RequestedPartyListAdapter(
//    val context: Context,
//    val list: MutableList<Friend>,
//    val itemClick: (Friend, Boolean) -> Unit
//) : RecyclerView.Adapter<RequestedPartyListAdapter.ViewHolder>() {
//
//    private val mDiffer = AsyncListDiffer(this, mFriendDiffCallback)
//
//    inner class ViewHolder(
//        val binding: ItemRequestedPartyBinding,
//        itemClick: (Friend, Boolean) -> Unit
//    ) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: Friend) {
//
//            item.picture?.let {
//                uploadImageFromCloud(it)
//            }
//
//            binding.tvRequestUserName.text = item.user_name
//
//            binding.btnAcceptRequest.setOnClickListener {
//                // TODO 서로의 친구 목록에 추가되도록
//                itemClick(item, true)
//            }
//
//            binding.btnRefusalRequest.setOnClickListener {
//                // TODO 요청 삭제
//                itemClick(item, false)
//            }
//
//        }
//
//        private fun uploadImageFromCloud(path: String) {
//            val imagesRef = Firebase.storage.reference.child(path)
//
//            imagesRef.downloadUrl.addOnCompleteListener { task ->
//                task.addOnSuccessListener {
//                    Glide.with(itemView)
//                        .load(it)
//                        .placeholder(R.drawable.loading_image)
//                        .override(binding.ivRequestUserPicture.width, binding.ivRequestUserPicture.height)
//                        .into(binding.ivRequestUserPicture)
//                }.addOnFailureListener {
//                    Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                if(task.isComplete) Log.d("Adapter", "성공")
//            }
//
//        }
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): ViewHolder {
//        val binding =
//            ItemRequestedPartyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding, itemClick)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = mDiffer.currentList[position]
//        holder.bind(item)
//    }
//
//    override fun getItemCount(): Int = mDiffer.currentList.size
//
//    override fun getItemId(position: Int): Long {
//        return mDiffer.currentList[position].hashCode().toLong()
//    }
//
//    fun submitList(newList: MutableList<Friend>) {
//        if(mDiffer.currentList == newList) return
//        else {
//            Log.d("RequestPartyListAdapter", "submitList")
//            newList?.let {
//                mDiffer.submitList(ArrayList<Friend>(newList))
//            }
//        }
//    }
//
//}

class RequestedPartyListAdapter(
    val itemClick: (FriendFirebase, Boolean) -> Unit
) : RecyclerView.Adapter<RequestedPartyListAdapter.ViewHolder>() {

    val requestedPartyList = mutableListOf<FriendFirebase>()

    private val mDiffer = AsyncListDiffer(this, mFriendFirebaseDiffCallback)

    inner class ViewHolder(
        val binding: ItemRequestedPartyBinding,
        itemClick: (FriendFirebase, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendFirebase) {

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

            imagesRef.downloadUrl.addOnCompleteListener { task ->
                task.addOnSuccessListener {
                    Glide.with(itemView)
                        .load(it)
                        .placeholder(R.drawable.loading_image)
                        .override(binding.ivRequestUserPicture.width, binding.ivRequestUserPicture.height)
                        .into(binding.ivRequestUserPicture)
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
        val binding =
            ItemRequestedPartyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = mDiffer.currentList[position]
//        holder.bind(item)
//    }
//
//    override fun getItemCount(): Int = mDiffer.currentList.size
//
//    override fun getItemId(position: Int): Long {
//        return mDiffer.currentList[position].hashCode().toLong()
//    }

    fun submitList(newList: MutableList<FriendFirebase>) {
        if(mDiffer.currentList == newList) return
        else {
            Log.d("RequestPartyListAdapter", "submitList")
            newList?.let {
                mDiffer.submitList(ArrayList<FriendFirebase>(newList))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(requestedPartyList[position])
    }

    override fun getItemCount() = requestedPartyList.size

}