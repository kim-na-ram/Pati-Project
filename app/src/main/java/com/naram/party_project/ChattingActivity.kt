package com.naram.party_project

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.naram.party_project.adapter.ChattingAdapter
import com.naram.party_project.base.BaseActivity
import com.naram.party_project.firebaseModel.ChatModel
import com.naram.party_project.databinding.ActivityChattingBinding
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_MESSAGE
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_USERS
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE

class ChattingActivity : BaseActivity<ActivityChattingBinding>({
    ActivityChattingBinding.inflate(it)
}) {

    private val TAG = "Chatting"

    private lateinit var chatModel: ChatModel
    private var chatRoomUID: String? = null
    private lateinit var myUID: String
    private lateinit var othersUID: String
    private lateinit var myName: String
    private lateinit var myPicture: String
    private lateinit var chattingRoomName: String

    private lateinit var chattingAdapter: ChattingAdapter

    private val firebaseDatabase = FirebaseDatabase.getInstance()

//    private val messageList = mutableListOf<ChatModel.Message>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getUserInformation()
        setRecyclerView()
        checkChattingRoom()
        initViews()

    }

    private fun getUserInformation() {
        if (intent.hasExtra("myUID"))
            myUID = intent.getStringExtra("myUID")!!

        if (intent.hasExtra("othersUID"))
            othersUID = intent.getStringExtra("othersUID")!!

        if (intent.hasExtra("chattingRoomName"))
            chattingRoomName = intent.getStringExtra("chattingRoomName")!!

        if (intent.hasExtra("chatRoomUID"))
            chatRoomUID = intent.getStringExtra("chatRoomUID")!!

        firebaseDatabase.reference.child(myUID).child(FIREBASE_USER)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    myName = snapshot.child(FIREBASE_USER_NAME).value.toString()
                    myPicture = snapshot.child(FIREBASE_USER_PICTURE).value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }

    private fun initViews() {

        setSupportActionBar(binding.layoutTop.toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)

        binding.layoutTop.tvTextToolbar.text = chattingRoomName
        binding.layoutTop.tvTextToolbar.textSize = 23F

        // 엔터키 이벤트 처리
//        binding.etMessage.setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
//                if(keyCode == KeyEvent.KEYCODE_ENTER) {
//                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(v?.windowToken, 0)
//
//                    return true
//                }
//                return false
//            }
//
//        })

        binding.btnSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun setRecyclerView() {
        chattingAdapter = ChattingAdapter(myUID)

        binding.rvChatting.adapter = chattingAdapter
        binding.rvChatting.layoutManager = LinearLayoutManager(this)
        binding.rvChatting.setHasFixedSize(true)
    }

    private fun sendMessage() {

        chatModel = ChatModel()
        chatModel.users[myUID] = true
        chatModel.users[othersUID] = true

        if (chatRoomUID == null) {
            firebaseDatabase.reference.child(FIREBASE_CHATTING).push().setValue(chatModel)
                .addOnSuccessListener {
                    checkChattingRoom()
                }.addOnFailureListener {

                }
        } else {
            sendMessageToDB()
        }
    }

    private fun checkChattingRoom() {
        firebaseDatabase.reference.child(FIREBASE_CHATTING)
            .orderByChild("$FIREBASE_CHATTING_USERS/$myUID")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val chatModel = it.getValue<ChatModel>()

                        chatModel?.let { chat ->
                            if (chat.users.containsKey(othersUID)) {
                                chatRoomUID = it.key!!
                                if (binding.etMessage.text.toString().isNotEmpty())
                                    sendMessageToDB()
                            }
                        }
                    }

                    if (chatRoomUID != null)
                        getMessageList()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

//        firebaseDatabase.reference.child(FIREBASE_CHATTING)
//            .get().addOnSuccessListener {
//                it.children.forEach { snapShot ->
//                    val chatModel = snapShot.getValue<ChatModel>()
//
//                    chatModel?.let {
//                        if(chatModel.users.containsKey(othersUID)) {
//                            chatRoomUID = snapShot.key
//                            sendMessageToDB()
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener {
//
//            }
    }


    private fun sendMessageToDB() {

        val message = ChatModel.Message()
        message.uid = myUID
        message.name = myName
        message.picture = myPicture
        message.message = binding.etMessage.text.toString()
        message.timestamp = System.currentTimeMillis()

        firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
            .child(FIREBASE_CHATTING_MESSAGE).push().setValue(message).addOnSuccessListener {
//                chattingAdapter.updateItem(message)
//                messageList.add(message)
//                chattingAdapter.submitList(messageList)
                binding.etMessage.setText("")
            }.addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun getMessageList() {
        firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
            .child(FIREBASE_CHATTING_MESSAGE).orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chattingAdapter.messageList.clear()
                    snapshot.children.forEach { data ->
                        data.getValue<ChatModel.Message>()?.let {
                            chattingAdapter.messageList.add(it)
                        }

                    }
                    chattingAdapter.notifyDataSetChanged()
//                    chattingAdapter.submitList(messageList)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


}