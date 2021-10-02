package com.naram.party_project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.callback.Party
import com.naram.party_project.databinding.ItemPartyBinding

class UserListAdapter(
    val context: Context,
    val list: MutableList<Party>,
    val itemClick: (Party) -> Unit) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPartyBinding, itemClick: (Party) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Party) {

            item.picture?.let {
                uploadImageFromCloud(it)
            }

            binding.tvPartyUserName.text = item.user_name

            item.self_pr?.let {
                binding.tvPartyUserPR.visibility = View.VISIBLE
                binding.tvPartyUserPR.text = it
                binding.tvPartyLeftQuote.visibility = View.VISIBLE
                binding.tvPartyRightQuote.visibility = View.VISIBLE
            }

            val gameList = mutableListOf<TextView>()

            if (item.game0?.toInt() == 1) gameList.add(binding.tvPartyGame0)
            if (item.game1?.toInt() == 1) gameList.add(binding.tvPartyGame1)
            if (item.game2?.toInt() == 1) gameList.add(binding.tvPartyGame2)
            if (item.game3?.toInt() == 1) gameList.add(binding.tvPartyGame3)
            if (item.game4?.toInt() == 1) gameList.add(binding.tvPartyGame4)
            if (item.game5?.toInt() == 1) gameList.add(binding.tvPartyGame5)
            if (item.game6?.toInt() == 1) gameList.add(binding.tvPartyGame6)
            if (item.game7?.toInt() == 1) gameList.add(binding.tvPartyGame7)
            if (item.game8?.toInt() == 1) gameList.add(binding.tvPartyGame8)
            if (item.game9?.toInt() == 1) gameList.add(binding.tvPartyGame9)

            if(gameList.size <= 6) {
                gameList.forEach {
                    it.visibility = View.VISIBLE
                }
            } else {
                gameList.forEachIndexed { index, textView ->
                    if(index > 5) binding.tvPartyGame10.visibility = View.VISIBLE
                    else textView.visibility = View.VISIBLE
                }
            }

            itemView.setOnClickListener {
                itemClick(item)
            }

        }

        private fun uploadImageFromCloud(path: String) {
            val imagesRef = Firebase.storage.reference.child(path)

            imagesRef.downloadUrl.addOnCompleteListener { task ->
                task.addOnSuccessListener {
                    Glide.with(itemView)
                        .load(it)
                        .placeholder(R.drawable.app_logo)
                        .override(binding.ivPartyUserPicture.width, binding.ivPartyUserPicture.height)
                        .into(binding.ivPartyUserPicture)
                }.addOnFailureListener {
                    Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }

                if(task.isComplete) Log.d("Adapter", "성공")
            }

//            val MAX_BYTE: Long = 400 * 400
//            imagesRef.getBytes(MAX_BYTE).addOnSuccessListener {
//                val options = BitmapFactory.Options();
//                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
//                binding.ivPartyUserPicture.setImageBitmap(bitmap)
//            }.addOnFailureListener {
//                Toast.makeText(binding.root.context, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPartyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    fun notifyData(newData : List<Party>) {
        Log.d("adapter", "notifyData")
        list.clear()
        list.addAll(newData)
        this.notifyDataSetChanged()
    }

    fun setData(newData: List<Party>) {
        Log.d("adapter", "setData")
        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, newData))
        list.clear()
        list.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateList(newData: List<Party>) {
        val diffCallback = DiffCallback(list, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        list.run {
            clear()
            addAll(newData)
            diffResult.dispatchUpdatesTo(this@UserListAdapter)
        }
    }

}