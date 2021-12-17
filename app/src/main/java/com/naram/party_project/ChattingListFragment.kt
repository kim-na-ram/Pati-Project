package com.naram.party_project

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.naram.party_project.base.BaseFragment
import com.naram.party_project.base.BaseViewDataFragment
import com.naram.party_project.databinding.FragmentChattinglistBinding
import com.naram.party_project.firebaseModel.ChatModel
import com.naram.party_project.firebaseModel.ChattingList
import com.naram.party_project.util.Const
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_MESSAGE
import com.naram.party_project.viewmodel.ChattingListViewModel
import com.naram.party_project.viewmodel.SearchPartyViewModel

class ChattingListFragment : BaseViewDataFragment<FragmentChattinglistBinding>(
    R.layout.fragment_chattinglist
) {

    private val TAG: String = "ChattingList"

    private lateinit var chattingListViewModel: ChattingListViewModel

    private lateinit var mDatabaseReference: DatabaseReference

    private val chatRoomList = mutableListOf<String>()
    private val chattingList = mutableListOf<ChattingList>()

    override fun init() {

        setViewModel()
        getChatRoomUIDList()

    }

    private fun setViewModel() {
        chattingListViewModel = ViewModelProvider(this).get(ChattingListViewModel::class.java)
        binding.viewModel = chattingListViewModel

        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun getChatRoomUIDList() {
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        val myUID = FirebaseAuth.getInstance().uid!!

        mDatabaseReference.child(FIREBASE_CHATTING)
            .orderByChild("${Const.FIREBASE_CHATTING_USERS}/$myUID")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        chatRoomList.add(it.key.toString())
                    }
                    getChattingList()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    private fun getChattingList() {
        chatRoomList.forEach { chatRoomUID ->
            Log.d(TAG, "$chatRoomUID")
            mDatabaseReference.child(FIREBASE_CHATTING).child(chatRoomUID)
                .child(FIREBASE_CHATTING_MESSAGE).orderByChild("timestamp")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val size = snapshot.childrenCount.toInt()
                        snapshot.children.forEachIndexed { index, dataSnapshot ->
                            if(size >= 1 && index == size - 1) {
                                dataSnapshot.getValue<ChatModel.Message>()?.let {
                                    Log.d(TAG, it.message)
                                    chattingList.add(
                                        ChattingList(
                                            chatRoomUID,
                                            it.name,
                                            it.picture,
                                            it.message,
                                            it.timestamp.toString()
                                        )
                                    )
                                }
                            }
                        }
                        chattingListViewModel.updateList(chattingList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


        }

    }
}