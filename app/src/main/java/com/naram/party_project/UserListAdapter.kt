package com.naram.party_project

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.callback.Party
import com.naram.party_project.DiffCallback
import com.naram.party_project.databinding.ItemPartyBinding

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    var list = mutableListOf<Party>()

//    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
//
//        val iv_userPicture = itemView.findViewById<ImageView>(R.id.iv_userPicture)
//        val tv_userName = itemView.findViewById<TextView>(R.id.tv_party_userName)
//        val tv_leftQuote = itemView.findViewById<TextView>(R.id.tv_party_leftQuote)
//        val tv_rightQuote = itemView.findViewById<TextView>(R.id.tv_party_rightQuote)
//        val tv_userPR = itemView.findViewById<TextView>(R.id.tv_party_userPR)
//
//        val tv_game0 = itemView.findViewById<TextView>(R.id.tv_party_game0)
//        val tv_game1 = itemView.findViewById<TextView>(R.id.tv_party_game1)
//        val tv_game2 = itemView.findViewById<TextView>(R.id.tv_party_game2)
//        val tv_game3 = itemView.findViewById<TextView>(R.id.tv_party_game3)
//        val tv_game4 = itemView.findViewById<TextView>(R.id.tv_party_game4)
//        val tv_game5 = itemView.findViewById<TextView>(R.id.tv_party_game5)
//        val tv_game6 = itemView.findViewById<TextView>(R.id.tv_party_game6)
//        val tv_game7 = itemView.findViewById<TextView>(R.id.tv_party_game7)
//        val tv_game8 = itemView.findViewById<TextView>(R.id.tv_party_game8)
//        val tv_game9 = itemView.findViewById<TextView>(R.id.tv_party_game9)
//
//        fun bind(item : Party) {
//            tv_userName.text = item.user_name
//            item.self_pr?.let {
//                tv_userPR.text = item.self_pr
//                tv_userPR.visibility = View.VISIBLE
//                tv_leftQuote.visibility = View.VISIBLE
//                tv_rightQuote.visibility = View.VISIBLE
//            }
//            item.picture?.let { uploadImageFromCloud(it) }
//
//            if(item.game0?.toInt() == 1)
//                tv_game0.visibility = View.VISIBLE
//            if(item.game1?.toInt() == 1)
//                tv_game1.visibility = View.VISIBLE
//            if(item.game2?.toInt() == 1)
//                tv_game2.visibility = View.VISIBLE
//            if(item.game3?.toInt() == 1)
//                tv_game3.visibility = View.VISIBLE
//            if(item.game4?.toInt() == 1)
//                tv_game4.visibility = View.VISIBLE
//            if(item.game5?.toInt() == 1)
//                tv_game5.visibility = View.VISIBLE
//            if(item.game6?.toInt() == 1)
//                tv_game6.visibility = View.VISIBLE
//            if(item.game7?.toInt() == 1)
//                tv_game7.visibility = View.VISIBLE
//            if(item.game8?.toInt() == 1)
//                tv_game8.visibility = View.VISIBLE
//            if(item.game9?.toInt() == 1)
//                tv_game9.visibility = View.VISIBLE
//        }
//
//        fun uploadImageFromCloud(path: String) {
//            val storage = Firebase.storage
//            var storageRef = storage.reference
//            var imagesRef = storageRef.child(path)
//
//            val flag = uploadImageFromURI(imagesRef)
//
//            if (!flag) {
//                uploadImageFromDownload(imagesRef)
//            }
//
//        }
//
//        private fun uploadImageFromURI(Ref: StorageReference): Boolean {
//            val result = Ref.downloadUrl.addOnSuccessListener {
//                Glide.with(itemView)
//                    .load(it)
//                    .into(iv_userPicture)
//            }
//
//            iv_userPicture.scaleType = ImageView.ScaleType.CENTER_CROP
//
//            return result.isSuccessful
//
//        }
//
//        private fun uploadImageFromDownload(Ref: StorageReference) {
//            val ONE_MEGABYTE: Long = 1024 * 1024
//            Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener {
//                val options = BitmapFactory.Options();
//                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
//                iv_userPicture.setImageBitmap(bitmap)
//                iv_userPicture.scaleType = ImageView.ScaleType.CENTER_CROP
//            }
//        }
//    }

    class ViewHolder(private val binding: ItemPartyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : Party) {

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

            if(item.game0?.toInt() == 1) binding.tvPartyGame0.visibility = View.VISIBLE
            if(item.game1?.toInt() == 1) binding.tvPartyGame1.visibility = View.VISIBLE
            if(item.game2?.toInt() == 1) binding.tvPartyGame2.visibility = View.VISIBLE
            if(item.game3?.toInt() == 1) binding.tvPartyGame3.visibility = View.VISIBLE
            if(item.game4?.toInt() == 1) binding.tvPartyGame4.visibility = View.VISIBLE
            if(item.game5?.toInt() == 1) binding.tvPartyGame5.visibility = View.VISIBLE
            if(item.game6?.toInt() == 1) binding.tvPartyGame6.visibility = View.VISIBLE
            if(item.game7?.toInt() == 1) binding.tvPartyGame7.visibility = View.VISIBLE
            if(item.game8?.toInt() == 1) binding.tvPartyGame8.visibility = View.VISIBLE
            if(item.game9?.toInt() == 1) binding.tvPartyGame9.visibility = View.VISIBLE
        }

        fun uploadImageFromCloud(path: String) {
            val storage = Firebase.storage
            var storageRef = storage.reference
            var imagesRef = storageRef.child(path)

            val flag = uploadImageFromURI(imagesRef)

            if (!flag) {
                uploadImageFromDownload(imagesRef)
            }

        }

        private fun uploadImageFromURI(Ref: StorageReference): Boolean {
            val result = Ref.downloadUrl.addOnSuccessListener {
                Glide.with(itemView)
                    .load(it)
                    .into(binding.ivPartyUserPicture)
            }

            binding.ivPartyUserPicture.scaleType = ImageView.ScaleType.CENTER_CROP

            return result.isSuccessful

        }

        private fun uploadImageFromDownload(Ref: StorageReference) {
            val ONE_MEGABYTE: Long = 1024 * 1024
            Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                val options = BitmapFactory.Options();
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
                binding.ivPartyUserPicture.setImageBitmap(bitmap)
                binding.ivPartyUserPicture.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_party, parent,false)
//        return ViewHolder(view)

        val binding = ItemPartyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun refreshRecyclerView(_list : MutableList<Party>) {
        list.clear()
        list.addAll(_list)
        this.notifyDataSetChanged()
    }

//    fun setData(newData: MutableList<Party>) {
//        val diffResult = DiffUtil.calculateDiff(DiffCallback.)
//        list = newData
//        diffResult.dispatchUpdatesTo(this)
//    }

}