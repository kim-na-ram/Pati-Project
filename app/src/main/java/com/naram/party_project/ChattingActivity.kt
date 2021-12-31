package com.naram.party_project

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.naram.party_project.adapter.ChattingAdapter
import com.naram.party_project.base.BaseActivity
import com.naram.party_project.chattingModel.Chatting
import com.naram.party_project.chattingModel.Message
import com.naram.party_project.databinding.ActivityChattingBinding
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_ISREAD
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_MESSAGE
import com.naram.party_project.util.Const.Companion.FIREBASE_CHATTING_USERS
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import com.naram.party_project.util.Const.Companion.FIREBASE_USERS
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE

class ChattingActivity : BaseActivity<ActivityChattingBinding>({
    ActivityChattingBinding.inflate(it)
}) {

    private val TAG = "Chatting"

    private lateinit var chatting: Chatting
    private var chatRoomUID: String? = null
    private lateinit var myUID: String
    private var othersUID: String? = null
    private lateinit var myName: String
    private var myPicture: String? = null
    private lateinit var othersName: String
    private var othersPicture: String? = null
    private lateinit var chatRoomName: String

    private lateinit var chattingAdapter: ChattingAdapter

    private val firebaseDatabase = FirebaseDatabase.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showProgressbar(true)
        getUserInformation()
        setRecyclerView()
        initViews()
        Handler(Looper.getMainLooper()).postDelayed({
            checkChattingRoom()
            showProgressbar(false)
        }, 2000)

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy")

        chatRoomUID?.let {
            firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
                .child(FIREBASE_CHATTING_USERS).child(myUID).child(FIREBASE_CHATTING_ISREAD).setValue(true)
        }

    }

    private fun showProgressbar(flag: Boolean) {

        if (flag) {
            binding.pbShowChattingProgress.visibility = View.VISIBLE
            val rotateAnimation =
                AnimationUtils.loadAnimation(baseContext, R.anim.animation_progressbar)
            binding.pbShowChattingProgress.startAnimation(rotateAnimation)
        } else {
            binding.pbShowChattingProgress.clearAnimation()
            binding.pbShowChattingProgress.visibility = View.GONE
        }

    }

    private fun getUserInformation() {
        if (intent.hasExtra("myUID"))
            myUID = intent.getStringExtra("myUID")!!

        if (intent.hasExtra("othersUID"))
            othersUID = intent.getStringExtra("othersUID")!!

        if (intent.hasExtra("chatRoomName"))
            chatRoomName = intent.getStringExtra("chatRoomName")!!

        if (intent.hasExtra("chatRoomUID"))
            chatRoomUID = intent.getStringExtra("chatRoomUID")!!

        when (chatRoomUID) {
            null -> {
                firebaseDatabase.reference.child(FIREBASE_USERS).child(myUID).child(FIREBASE_USER)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            myName = snapshot.child(FIREBASE_USER_NAME).value.toString()
                            myPicture =
                                if (snapshot.child(FIREBASE_USER_PICTURE).exists()) snapshot.child(
                                    FIREBASE_USER_PICTURE
                                ).value.toString() else null
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, error.message)
                        }

                    })

                firebaseDatabase.reference.child(FIREBASE_USERS).child(othersUID!!)
                    .child(FIREBASE_USER)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            othersName = snapshot.child(FIREBASE_USER_NAME).value.toString()
                            othersPicture = if (snapshot.child(FIREBASE_USER_PICTURE).exists()) snapshot.child(
                                FIREBASE_USER_PICTURE
                            ).value.toString() else null
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, error.message)
                        }

                    })
            }
            else -> {

                firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
                    .child(FIREBASE_CHATTING_USERS)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach { ds ->
                                val userInfo = ds.getValue<Chatting.UserInfo>()
                                Log.d(TAG, "$userInfo")
                                when (ds.key) {
                                    myUID -> {
                                        userInfo?.let {
                                            myName = it.name
                                            myPicture = it.picture
                                        }
                                    }
                                    else -> {
                                        othersUID = ds.key
                                        userInfo?.let {
                                            othersName = it.name
                                            othersPicture = it.picture
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, error.message)
                        }

                    })
            }
        }

    }

    private fun initViews() {

        setSupportActionBar(binding.layoutTop.toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)

        binding.layoutTop.tvTextToolbar.text = chatRoomName
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

        val myInfo = Chatting.UserInfo()
        val othersInfo = Chatting.UserInfo()

        myInfo.name = myName
        myInfo.picture = myPicture

        othersInfo.name = othersName
        othersInfo.picture = othersPicture

        chatting = Chatting()
        chatting.users[myUID] = myInfo
        chatting.users[othersUID] = othersInfo

        Log.d(TAG, "chatRoomUID : $chatRoomUID")

        if (chatRoomUID == null) {
            firebaseDatabase.reference.child(FIREBASE_CHATTING).push().setValue(chatting)
                .addOnSuccessListener {
                    checkChattingRoom()
                }.addOnFailureListener {
                    Log.e(TAG, it.stackTraceToString())
                }
        } else {
            sendMessageToDB()
        }
    }

    private fun checkChattingRoom() {
        firebaseDatabase.reference.child(FIREBASE_CHATTING)
            .orderByChild("$FIREBASE_CHATTING_USERS/$myUID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val chatModel = it.getValue<Chatting>()

                        chatModel?.let { chat ->
                            if (chat.users.containsKey(myUID)
                                && chat.users.containsKey(othersUID)
                            ) {
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
                    Log.e(TAG, error.message)
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

        val message = Chatting.Message()
        message.uid = myUID
        message.message = binding.etMessage.text.toString()
        message.timestamp = System.currentTimeMillis()

        firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
            .child(FIREBASE_CHATTING_USERS).child(othersUID!!).child(FIREBASE_CHATTING_ISREAD)
            .setValue(false)

        firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
            .child(FIREBASE_CHATTING_MESSAGE).push().setValue(message).addOnSuccessListener {
                binding.etMessage.setText("")
            }.addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun getMessageList() {

        firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
            .child(FIREBASE_CHATTING_USERS).child(myUID).child(FIREBASE_CHATTING_ISREAD).setValue(true)

        firebaseDatabase.reference.child(FIREBASE_CHATTING).child(chatRoomUID!!)
            .child(FIREBASE_CHATTING_MESSAGE).orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chattingAdapter.messageList.clear()
                    snapshot.children.forEach { data ->

                        data.getValue<Chatting.Message>()?.let {
                            chattingAdapter.messageList.add(
                                Message(
                                    it.uid,
                                    othersName,
                                    othersPicture,
                                    it.message,
                                    it.timestamp
                                )
                            )
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