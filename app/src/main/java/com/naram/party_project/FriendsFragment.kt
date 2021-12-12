package com.naram.party_project

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.adapter.FriendListAdapter
import com.naram.party_project.adapter.RequestedPartyListAdapter
import com.naram.party_project.base.BaseFragment
import com.naram.party_project.callback.Friend
import com.naram.party_project.callback.FriendFirebase
import com.naram.party_project.callback.PartyFirebase
import com.naram.party_project.databinding.FragmentFriendsBinding
import com.naram.party_project.util.Const.Companion.FIREBASE_FRIEND
import com.naram.party_project.util.Const.Companion.FIREBASE_FRIEND_YES
import com.naram.party_project.util.Const.Companion.FIREBASE_PARTY
import com.naram.party_project.util.Const.Companion.FIREBASE_PARTY_WAIT
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_EMAIL
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsFragment : BaseFragment<FragmentFriendsBinding>() {

    private val TAG = "Friends"

    private lateinit var requestedPartyListAdapter: RequestedPartyListAdapter
    private lateinit var friendListAdapter: FriendListAdapter

    private val requestUidList: MutableList<String> = mutableListOf()
    private val requestList: MutableList<FriendFirebase> = mutableListOf()
    private val friendUidList: MutableList<String> = mutableListOf()
    private val friendList: MutableList<FriendFirebase> = mutableListOf()

    private val uid = FirebaseAuth.getInstance().uid

    private var changeRequestedPartyFlag = false

//    private val request_list: MutableList<Friend> = mutableListOf()
//    private val friend_list: MutableList<Friend> = mutableListOf()

//    private var request_flag = false
//    private var friend_flag = false

    override fun getFragmentBinding() = FragmentFriendsBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

        setPartyRecyclerView()
        setRequestedPartyListFromFirebase()

        setFriendRecyclerView()
        setFriendListFromFirebase()

//        getRequestedPartyList()
//        getFriendList()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        requestUidList.clear()
        requestList.clear()

        friendUidList.clear()
        friendList.clear()

    }

    private fun requestedPartyShowSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.slRequestedPartyShimmer.startShimmer()
            binding.rvRequestedParty.visibility = View.INVISIBLE
            binding.slRequestedPartyShimmer.visibility = View.VISIBLE
        } else {
            binding.slRequestedPartyShimmer.stopShimmer()
            binding.rvRequestedParty.visibility = View.VISIBLE
            binding.slRequestedPartyShimmer.visibility = View.INVISIBLE
        }
    }

    private fun friendsShowSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.slFriendShimmer.startShimmer()
            binding.rvFriends.visibility = View.INVISIBLE
            binding.slFriendShimmer.visibility = View.VISIBLE
        } else {
            binding.slFriendShimmer.stopShimmer()
            binding.rvFriends.visibility = View.VISIBLE
            binding.slFriendShimmer.visibility = View.INVISIBLE
        }
    }

    private fun setPartyRecyclerView() {

        requestedPartyListAdapter =
            RequestedPartyListAdapter(
                mutableListOf()
            ) { it, flag ->

                if (flag) {
                    addFirebaseFriend(it)

//                    addFriend(email!!, s)
//                    removeRequestedParty(email!!, s)
//                    friendListAdapter.submitList(friendList)
//                    requestedPartyListAdapter.submitList(requestList)
//                    onResume()
                } else {
                    removeFirebaseParty(it)

//                    removeRequestedParty(email!!, it)
//                    requestedPartyListAdapter.submitList(requestList)
//                    onResume()
                }
            }

        requestedPartyListAdapter.setHasStableIds(true)
        binding.rvRequestedParty.adapter = requestedPartyListAdapter

    }

    private fun setFriendRecyclerView() {

        friendListAdapter =
            FriendListAdapter(
                mutableListOf()
            ) { it, flag ->

                if (flag) {
                    // TODO it.email 에게 메시지 보내기
                    // sendMessage()
                } else {
                    // TODO 친구 삭제하기
                    removeFirebaseFriend(it)

//                    removeFriend(email!!, friend_email)
//                    onResume()
                }

            }

        friendListAdapter.setHasStableIds(true)
        binding.rvFriends.adapter = friendListAdapter

    }

    private fun setRequestedPartyListFromFirebase() {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.get()
            .addOnSuccessListener { dataSnapshot ->
                dataSnapshot.child(FIREBASE_PARTY).child(uid!!).children.forEach {
                    if (it.exists()) {
                        if (it.value!! == FIREBASE_PARTY_WAIT
                            && !requestUidList.contains(it.key.toString())
                        ) {
                            requestUidList.add(it.key!!)
                        }
                    }
                }

                addFirebasePartyList(mDatabaseReference)

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }

    }

    private fun addFirebasePartyList(mDatabaseReference: DatabaseReference) {

        var tmpEmail = ""
        var tmpName = ""
        var tmpPicture: String? = null

        Log.d(TAG, "requestUidList size is ${requestUidList.size}")

        requestUidList.forEach {
            mDatabaseReference.child(it).child(FIREBASE_USER).get()
                .addOnSuccessListener { ds ->
                    requestList.add(
                        FriendFirebase(
                            it,
                            ds.child(FIREBASE_USER_EMAIL).value.toString(),
                            ds.child(FIREBASE_USER_NAME).value.toString(),
                            ds.child(FIREBASE_USER_PICTURE).value.toString()
                        )
                    )
                    requestedPartyListAdapter.submitList(requestList)
                    if (binding.slRequestedPartyShimmer.visibility == View.VISIBLE)
                        requestedPartyShowSampleData(false)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "요청받은 파티 리스트를 가져오는 것에 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, e.stackTraceToString())
                }
        }

    }

    private fun removeFirebaseParty(partyFirebase: FriendFirebase) {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.child(FIREBASE_PARTY).child(uid!!).child(partyFirebase.uid)
            .removeValue()
            .addOnFailureListener {
                Log.d(TAG, it.stackTraceToString())
            }

        requestUidList.remove(partyFirebase.uid)
        requestList.remove(partyFirebase)

        requestedPartyListAdapter.submitList(requestList)

    }

    private fun setFriendListFromFirebase() {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.get()
            .addOnSuccessListener { dataSnapshot ->
                dataSnapshot.child(FIREBASE_FRIEND).child(uid!!).children.forEach {
                    if (it.exists()) {
                        if (it.value!! == FIREBASE_FRIEND_YES
                            && !friendUidList.contains(it.key.toString())
                        ) {
                            friendUidList.add(it.key!!)
                        }
                    }
                }

                addFirebaseFriendList(mDatabaseReference)

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addFirebaseFriendList(mDatabaseReference: DatabaseReference) {

        var tmpEmail = ""
        var tmpName = ""
        var tmpPicture: String? = null

        Log.d(TAG, "friendUidList size is ${friendUidList.size}")

        friendUidList.forEach {
            mDatabaseReference.child(it).child(FIREBASE_USER).get()
                .addOnSuccessListener { ds ->
                    friendList.add(
                        FriendFirebase(
                            it,
                            ds.child(FIREBASE_USER_EMAIL).value.toString(),
                            ds.child(FIREBASE_USER_NAME).value.toString(),
                            ds.child(FIREBASE_USER_PICTURE).value.toString()
                        )
                    )
                    friendListAdapter.submitList(friendList)
                    if (binding.slFriendShimmer.visibility == View.VISIBLE)
                        friendsShowSampleData(false)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "친구 리스트를 가져오는 것에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, e.stackTraceToString())
                }
        }

    }

    private fun addFirebaseFriend(partyFirebase: FriendFirebase) {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.child(FIREBASE_PARTY).child(uid!!).child(partyFirebase.uid)
            .removeValue()
            .addOnFailureListener {
            Log.d(TAG, it.stackTraceToString())
        }

        mDatabaseReference.child(FIREBASE_FRIEND).child(uid!!).child(partyFirebase.uid)
            .setValue(FIREBASE_FRIEND_YES)

        friendUidList.add(partyFirebase.uid)
        friendList.add(partyFirebase)

        requestUidList.remove(partyFirebase.uid)
        requestList.remove(partyFirebase)

        requestedPartyListAdapter.submitList(requestList)
        friendListAdapter.submitList(friendList)

    }

    private fun removeFirebaseFriend(friendFirebase: FriendFirebase) {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.child(FIREBASE_FRIEND).child(uid!!).child(friendFirebase.uid)
            .removeValue()
            .addOnFailureListener {
                Log.d(TAG, it.stackTraceToString())
            }

        friendUidList.remove(friendFirebase.uid)
        friendList.remove(friendFirebase)

        friendListAdapter.submitList(friendList)

    }


//    private fun getRequestedPartyList() {
//
//        val retrofit = RetrofitClient.getInstance()
//
//        val server = retrofit.create(UserAPI::class.java)
//
//        val email = FirebaseAuth.getInstance().currentUser?.email
//
//        email?.let {
//            server.getRequestedParty(email).enqueue(object : Callback<List<Friend>> {
//                override fun onResponse(
//                    call: Call<List<Friend>>,
//                    response: Response<List<Friend>>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d(TAG, "getRequestedParty 성공 : ${response.body()}")
//                        request_list.clear()
//                        response.body()?.forEachIndexed { index, friend ->
//                            if (friend.status == "true") {
//                                request_list.add(friend)
//                                friend.picture?.let { path ->
//                                    val MAX_BYTE: Long = 400 * 400
//                                    val imagesRef = Firebase.storage.reference.child(path)
//
//                                    imagesRef.getBytes(MAX_BYTE).addOnSuccessListener {
//                                        val options = BitmapFactory.Options()
//                                        val bitmap =
//                                            BitmapFactory.decodeByteArray(it, 0, it.size, options)
//                                        request_list[index].bitmap = bitmap
//                                    }.addOnFailureListener {
//                                        Toast.makeText(
//                                            binding.root.context,
//                                            "사진을 불러오는데 오류가 발생했습니다.",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                            }
//                        }
//                        request_flag = true
//                    } else {
//                        Log.d(TAG, "getRequestedParty 실패 : ${response.errorBody().toString()}")
//                        request_list.clear()
//                    }
//                }
//
//                override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
//                    Log.d(TAG, "getRequestedParty onFailure : ${t.localizedMessage}")
//                    request_list.clear()
//                }
//            })
//        }
//
//    }
//
//    private fun getFriendList() {
//
//        val retrofit = RetrofitClient.getInstance()
//
//        val server = retrofit.create(UserAPI::class.java)
//
//        val email = FirebaseAuth.getInstance().currentUser?.email
//
//        email?.let {
//            server.getFriend(email).enqueue(object : Callback<List<Friend>> {
//                override fun onResponse(
//                    call: Call<List<Friend>>,
//                    response: Response<List<Friend>>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d(TAG, "getFriend 성공 : ${response.body()}")
//                        friend_list.clear()
//                        response.body()?.forEachIndexed { index, friend ->
//                            if (friend.status == "true") {
//                                friend_list.add(friend)
//                                friend.picture?.let { path ->
//                                    val MAX_BYTE: Long = 400 * 400
//                                    val imagesRef = Firebase.storage.reference.child(path)
//
//                                    imagesRef.getBytes(MAX_BYTE).addOnSuccessListener {
//                                        val options = BitmapFactory.Options()
//                                        val bitmap =
//                                            BitmapFactory.decodeByteArray(it, 0, it.size, options)
//                                        friend_list[index].bitmap = bitmap
//                                    }.addOnFailureListener {
//                                        Toast.makeText(
//                                            binding.root.context,
//                                            "사진을 불러오는데 오류가 발생했습니다.",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                            }
//                        }
//                        friend_flag = true
//                    } else {
//                        Log.d(TAG, "getFriend 실패 : ${response.errorBody().toString()}")
//                        friend_list.clear()
//                    }
//                }
//
//                override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
//                    Log.d(TAG, "getFriend onFailure : ${t.localizedMessage}")
//                    friend_list.clear()
//                }
//            })
//        }
//
//    }

    private fun addFriend(email: String, friend_email: String) {

//        request_list.forEachIndexed { index, friend ->
//            if (friend.email == friend_email) {
//                friend_list.add(friend)
//                return@forEachIndexed
//            }
//        }
//
//        Log.d(TAG, friend_list.toString())
//
//        val retrofit = RetrofitClient.getInstance()
//
//        val server = retrofit.create(UserAPI::class.java)
//
//        val call: Call<String> = server.putFriend(email, friend_email)
//        call.enqueue(object : Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.d(TAG, "onFailure : ${t.localizedMessage}")
//            }
//
//            override fun onResponse(
//                call: Call<String>,
//                response: Response<String>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "성공 : ${response.body()}")
//                } else {
//                    Log.d(TAG, "실패 : ${response.errorBody()}")
//                }
//            }
//        })

    }

    private fun removeRequestedParty(receive_email: String, request_email: String) {

//        if (request_list.size > 1) {
//            var idx = 0
//            request_list.forEachIndexed { index, it ->
//                if (it.email == request_email) {
//                    idx = index
//                    return@forEachIndexed
//                }
//            }
//
//            request_list.removeAt(idx)
//        } else request_list.clear()
//
//        Log.d(TAG, request_list.toString())
//
//        val retrofit = RetrofitClient.getInstance()
//
//        val server = retrofit.create(UserAPI::class.java)
//
//        val call: Call<String> = server.delRequestedParty(request_email, receive_email)
//        call.enqueue(object : Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.d(TAG, "onFailure : ${t.localizedMessage}")
//            }
//
//            override fun onResponse(
//                call: Call<String>,
//                response: Response<String>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "성공 : ${response.body()}")
//                } else {
//                    Log.d(TAG, "실패 : ${response.errorBody()}")
//                }
//            }
//        })

    }

    private fun removeFriend(email: String, friend_email: String) {

//        val retrofit = RetrofitClient.getInstance()
//
//        val server = retrofit.create(UserAPI::class.java)
//
//        val call: Call<String> = server.delFriend(email, friend_email)
//        call.enqueue(object : Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.d(TAG, "onFailure : ${t.localizedMessage}")
//            }
//
//            override fun onResponse(
//                call: Call<String>,
//                response: Response<String>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "성공 : ${response.body()}")
//
//                    if (friend_list.size > 1) {
//                        var idx = 0
//                        friend_list.forEachIndexed { index, it ->
//                            if (it.email == friend_email) {
//                                idx = index
//                                return@forEachIndexed
//                            }
//                        }
//
//                        friend_list.removeAt(idx)
//                    } else friend_list.clear()
//
//                    Log.d(TAG, "$friend_list")
//
//                    friendListAdapter.submitList(friend_list)
//
//                } else {
//                    Log.d(TAG, "실패 : ${response.errorBody()}")
//                }
//            }
//        })

    }

}