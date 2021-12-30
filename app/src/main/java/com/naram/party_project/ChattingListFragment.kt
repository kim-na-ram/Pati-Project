package com.naram.party_project

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.naram.party_project.adapter.ChattingAdapter
import com.naram.party_project.adapter.ChattingListAdapter
import com.naram.party_project.adapter.ChattingListBindingAdapter
import com.naram.party_project.base.BaseViewDataFragment
import com.naram.party_project.databinding.FragmentChattinglistBinding
import com.naram.party_project.chattingModel.Chatting
import com.naram.party_project.chattingModel.ChattingList
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_ISREAD
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_MESSAGE
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_USERS
import com.naram.party_project.viewmodel.ChattingListViewModel
import kotlinx.coroutines.runBlocking

class ChattingListFragment : BaseViewDataFragment<FragmentChattinglistBinding>(
    R.layout.fragment_chattinglist
) {

    private val TAG: String = "ChattingList"

    private lateinit var chattingListViewModel: ChattingListViewModel

    private lateinit var mDatabaseReference: DatabaseReference

    private val chatRoomList = mutableMapOf<String, Chatting.UserInfo>()
    private val chattingList = mutableListOf<ChattingList>()

    override fun init() {

        setViewModel()
        setRecyclerView()
        getChatRoomUIDList()
//        chattingListViewModel.currentList.value.let {
//            Log.d(TAG, "chattingListViewModel is Changed")
//            chatRoomList.clear()
//            chattingList.clear()
//            getChatRoomUIDList()
//        }

    }

    private fun setViewModel() {
        chattingListViewModel = ViewModelProvider(this)[ChattingListViewModel::class.java]
        binding.viewModel = chattingListViewModel

        binding.lifecycleOwner = viewLifecycleOwner

    }

    private fun setRecyclerView() {
        val adapter = ChattingListAdapter { chattingList ->
            val intent = Intent(activity, ChattingActivity::class.java)
            intent.putExtra("chatRoomUID", chattingList.chatRoomUID)
            intent.putExtra("chatRoomName", chattingList.receivedName)
            intent.putExtra("myUID", chattingList.myUID)

            startActivity(intent)
        }

        adapter.setHasStableIds(true)
//        val customDecoration = DividerItemDecoration(context, VERTICAL)
//        binding.rvChattingList.addItemDecoration(customDecoration)
        binding.rvChattingList.adapter = adapter
    }

    private fun getChatRoomUIDList() {
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        val myUID = FirebaseAuth.getInstance().uid!!

        mDatabaseReference.child(FIREBASE_CHATTING)
            .orderByChild("${FIREBASE_CHATTING_USERS}/$myUID")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val ds = it.child(FIREBASE_CHATTING_USERS)
                        ds.children.forEach { dds ->
                            if (dds.key != myUID && ds.hasChild(myUID)) {
                                val userInfo = dds.getValue<Chatting.UserInfo>()!!
                                userInfo.isRead = ds.child(myUID).child(FIREBASE_CHATTING_ISREAD).value.toString().toBoolean()
                                chatRoomList[it.key.toString()] = userInfo
                            }
                        }
                    }
                    getChattingList()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    private fun getChattingList() {
        chatRoomList.forEach { (chatRoomUID, userInfo) ->
            Log.d(TAG, "$chatRoomUID")
            mDatabaseReference.child(FIREBASE_CHATTING).child(chatRoomUID)
                .child(FIREBASE_CHATTING_MESSAGE).orderByChild("timestamp")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEachIndexed { index, dataSnapshot ->
                            if (snapshot.childrenCount.toInt() >= 1
                                && index == snapshot.childrenCount.toInt() - 1
                            ) {
                                dataSnapshot.getValue<Chatting.Message>()?.let {
                                    val newChattingList = ChattingList(
                                        chatRoomUID,
                                        FirebaseAuth.getInstance().uid!!,
                                        othersUID = null,
                                        userInfo.name,
                                        userInfo.picture,
                                        it.message,
                                        it.timestamp.toString(),
                                        userInfo.isRead
                                    )
                                    if (!chattingList.contains(newChattingList)) {
                                        var flag = false
                                        chattingList.forEachIndexed { index, oldChattingList ->
                                            if ((oldChattingList.chatRoomUID == newChattingList.chatRoomUID)
                                                && (oldChattingList.lastMessage != newChattingList.lastMessage
                                                        || oldChattingList.isRead != newChattingList.isRead)
                                            ) {
                                                Log.e(TAG, "origin changed : $newChattingList")
                                                flag = true
                                                chattingList[index] = newChattingList
                                            }
                                        }
                                        if(!flag) {
                                            Log.e(TAG, "new : $newChattingList")
                                            chattingList.add(newChattingList)
                                        }
                                    } else {
                                        chattingList.forEachIndexed { index, oldChattingList ->
                                            if ((oldChattingList.chatRoomUID == newChattingList.chatRoomUID)
                                                && (oldChattingList.lastMessage != newChattingList.lastMessage
                                                        || oldChattingList.isRead != newChattingList.isRead)
                                            ) {
                                                Log.e(TAG, "changed : $newChattingList")
                                                chattingList[index] = newChattingList
                                            }
                                        }
                                    }
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