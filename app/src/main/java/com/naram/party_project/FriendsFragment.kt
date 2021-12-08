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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.adapter.FriendListAdapter
import com.naram.party_project.adapter.RequestedPartyListAdapter
import com.naram.party_project.callback.Friend
import com.naram.party_project.databinding.FragmentFriendsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.timer

class FriendsFragment : Fragment() {

    private val TAG = "Friends"

    private lateinit var mainActivity: MainActivity

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestedPartyListAdapter: RequestedPartyListAdapter
    private lateinit var friendListAdapter: FriendListAdapter

    private val request_list: MutableList<Friend> = mutableListOf()
    private val friend_list: MutableList<Friend> = mutableListOf()

    private var request_flag = false
    private var friend_flag = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

//        setPartyRecyclerView()
//        setFriendRecyclerView()
//
//        getRequestedPartyList()
//        getFriendList()
//
//        requestedPartyLoadSampleData()
//        friendsLoadSampleData()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun requestedPartyLoadSampleData() {

        requestedPartyShowSampleData(isLoading = true)

        timer(period = 3000) {
            if (request_flag) {
                activity?.runOnUiThread {
                    requestedPartyListAdapter.submitList(request_list)
                    requestedPartyShowSampleData(isLoading = false)
                }

                this.cancel()
            }
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            if (request_flag)
//                activity?.runOnUiThread {
//                    requestedPartyShowSampleData(isLoading = false)
//                }
//        }, 4000)

    }

    private fun friendsLoadSampleData() {

        friendsShowSampleData(isLoading = true)

        timer(period = 3000) {
            if (friend_flag) {
                activity?.runOnUiThread {
                    friendListAdapter.submitList(friend_list)
                    friendsShowSampleData(isLoading = false)
                }

                this.cancel()
            }
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            if (friend_flag)
//                activity?.runOnUiThread {
//                    friendsShowSampleData(isLoading = false)
//                }
//        }, 4000)

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

    private fun getRequestedPartyList() {

        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

        val email = FirebaseAuth.getInstance().currentUser?.email

        email?.let {
            server.getRequestedParty(email).enqueue(object : Callback<List<Friend>> {
                override fun onResponse(
                    call: Call<List<Friend>>,
                    response: Response<List<Friend>>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "getRequestedParty 성공 : ${response.body()}")
                        request_list.clear()
                        response.body()?.forEachIndexed { index, friend ->
                            if (friend.status == "true") {
                                request_list.add(friend)
                                friend.picture?.let { path ->
                                    val MAX_BYTE: Long = 400 * 400
                                    val imagesRef = Firebase.storage.reference.child(path)

                                    imagesRef.getBytes(MAX_BYTE).addOnSuccessListener {
                                        val options = BitmapFactory.Options()
                                        val bitmap =
                                            BitmapFactory.decodeByteArray(it, 0, it.size, options)
                                        request_list[index].bitmap = bitmap
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            binding.root.context,
                                            "사진을 불러오는데 오류가 발생했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                        request_flag = true
                    } else {
                        Log.d(TAG, "getRequestedParty 실패 : ${response.errorBody().toString()}")
                        request_list.clear()
                    }
                }

                override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                    Log.d(TAG, "getRequestedParty onFailure : ${t.localizedMessage}")
                    request_list.clear()
                }
            })
        }

    }

    private fun getFriendList() {

        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

        val email = FirebaseAuth.getInstance().currentUser?.email

        email?.let {
            server.getFriend(email).enqueue(object : Callback<List<Friend>> {
                override fun onResponse(
                    call: Call<List<Friend>>,
                    response: Response<List<Friend>>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "getFriend 성공 : ${response.body()}")
                        friend_list.clear()
                        response.body()?.forEachIndexed { index, friend ->
                            if (friend.status == "true") {
                                friend_list.add(friend)
                                friend.picture?.let { path ->
                                    val MAX_BYTE: Long = 400 * 400
                                    val imagesRef = Firebase.storage.reference.child(path)

                                    imagesRef.getBytes(MAX_BYTE).addOnSuccessListener {
                                        val options = BitmapFactory.Options()
                                        val bitmap =
                                            BitmapFactory.decodeByteArray(it, 0, it.size, options)
                                        friend_list[index].bitmap = bitmap
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            binding.root.context,
                                            "사진을 불러오는데 오류가 발생했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                        friend_flag = true
                    } else {
                        Log.d(TAG, "getFriend 실패 : ${response.errorBody().toString()}")
                        friend_list.clear()
                    }
                }

                override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                    Log.d(TAG, "getFriend onFailure : ${t.localizedMessage}")
                    friend_list.clear()
                }
            })
        }

    }

    private fun addFriend(email: String, friend_email: String) {

        request_list.forEachIndexed { index, friend ->
            if (friend.email == friend_email) {
                friend_list.add(friend)
                return@forEachIndexed
            }
        }

        Log.d(TAG, friend_list.toString())

        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

        val call: Call<String> = server.putFriend(email, friend_email)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG, "onFailure : ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "성공 : ${response.body()}")
                } else {
                    Log.d(TAG, "실패 : ${response.errorBody()}")
                }
            }
        })

    }

    private fun removeRequestedParty(receive_email: String, request_email: String) {

        if(request_list.size > 1) {
            var idx = 0
            request_list.forEachIndexed { index, it ->
                if (it.email == request_email) {
                    idx = index
                    return@forEachIndexed
                }
            }

            request_list.removeAt(idx)
        } else request_list.clear()

        Log.d(TAG, request_list.toString())

        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

        val call: Call<String> = server.delRequestedParty(request_email, receive_email)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG, "onFailure : ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "성공 : ${response.body()}")
                } else {
                    Log.d(TAG, "실패 : ${response.errorBody()}")
                }
            }
        })

    }

    private fun removeFriend(email: String, friend_email: String) {

        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

        val call: Call<String> = server.delFriend(email, friend_email)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG, "onFailure : ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "성공 : ${response.body()}")

                    if(friend_list.size > 1) {
                        var idx = 0
                        friend_list.forEachIndexed { index, it ->
                            if (it.email == friend_email) {
                                idx = index
                                return@forEachIndexed
                            }
                        }

                        friend_list.removeAt(idx)
                    } else friend_list.clear()

                    Log.d(TAG, "$friend_list")

                    friendListAdapter.submitList(friend_list)

                } else {
                    Log.d(TAG, "실패 : ${response.errorBody()}")
                }
            }
        })

    }

    private fun setPartyRecyclerView() {

        requestedPartyListAdapter =
            RequestedPartyListAdapter(
                requireContext(),
                mutableListOf()
            ) { it, flag ->
                val email = FirebaseAuth.getInstance().currentUser?.email
                val request_email = it.email

                if (flag) {
                    request_email?.let {
                        addFriend(email!!, it)
                        removeRequestedParty(email!!, it)
                        friendListAdapter.submitList(friend_list)
                        requestedPartyListAdapter.submitList(request_list)
                        onResume()
                    }
                } else {
                    request_email?.let {
                        removeRequestedParty(email!!, it)
                        requestedPartyListAdapter.submitList(request_list)
                        onResume()

                    }
                }
            }

        requestedPartyListAdapter.setHasStableIds(true)
        binding.rvRequestedParty.adapter = requestedPartyListAdapter

    }

    private fun setFriendRecyclerView() {

        friendListAdapter =
            FriendListAdapter(
                requireContext(),
                mutableListOf()
            ) { it, flag ->
                val email = FirebaseAuth.getInstance().currentUser?.email
                val friend_email = it.email

                if (flag) {
                    // TODO it.email 에게 메시지 보내기
                    // sendMessage()
                } else {
                    // TODO 친구 삭제하기
                    friend_email?.let {
                        removeFriend(email!!, friend_email)
                        onResume()
                    }
                }

            }

        friendListAdapter.setHasStableIds(true)
        binding.rvFriends.adapter = friendListAdapter

    }

    fun refreshLayout() {
        request_flag = false
        friend_flag = false

        getRequestedPartyList()
        getFriendList()

        requestedPartyLoadSampleData()
        friendsLoadSampleData()
    }

}