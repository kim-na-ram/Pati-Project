package com.naram.party_project

import android.util.Log
import androidx.lifecycle.ViewModelProvider
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
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_MESSAGE
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_USERS
import com.naram.party_project.viewmodel.ChattingListViewModel

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
        initViews()
//        getChatRoomUIDList()

    }

    override fun onResume() {
        super.onResume()

        chattingListViewModel.currentList.value.let {
            chatRoomList.clear()
            chattingList.clear()
            getChatRoomUIDList()
        }

    }

    private fun setViewModel() {
        chattingListViewModel = ViewModelProvider(this)[ChattingListViewModel::class.java]
        binding.viewModel = chattingListViewModel

        binding.lifecycleOwner = viewLifecycleOwner

    }

    private fun initViews() {
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
                            if(dds.key != myUID && ds.hasChild(myUID))
                                chatRoomList[it.key.toString()] = dds.getValue<Chatting.UserInfo>()!!
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
                                    val cl = ChattingList(
                                        chatRoomUID,
                                        FirebaseAuth.getInstance().uid!!,
                                        othersUID = null,
                                        userInfo.name,
                                        userInfo.picture,
                                        it.message,
                                        it.timestamp.toString()
                                    )
                                    if(!chattingList.contains(cl)) {
                                        Log.d(TAG, "${userInfo.name} : ${it.message}")
                                        chattingList.add(cl)
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