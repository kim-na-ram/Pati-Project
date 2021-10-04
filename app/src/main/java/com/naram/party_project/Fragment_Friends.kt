package com.naram.party_project

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.naram.party_project.adapter.FriendListAdapter
import com.naram.party_project.adapter.RequestedPartyListAdapter
import com.naram.party_project.callback.Friend
import com.naram.party_project.databinding.FragmentFriendsBinding
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Fragment_Friends : Fragment() {

    private val TAG = "Friends"

    private lateinit var mainActivity: MainActivity

    private val request_list: MutableList<Friend> = mutableListOf()
    private val friend_list: MutableList<Friend> = mutableListOf()

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    lateinit var requestedPartyListAdapter: RequestedPartyListAdapter
    lateinit var friendListAdapter: FriendListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

        runBlocking {
            getRequestedPartyList()
            getFriendList()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun getRequestedPartyList() {

        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

//        server.getRequestedParty(email!!).enqueue(object : Callback<String> {
//            override fun onResponse(
//                call: Call<String>,
//                response: Response<String>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "성공 : ${response.body()}")
//                } else {
//                    Log.d(TAG, "실패 : ${response.errorBody()}")
//                    request_list.clear()
//                }
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.d(TAG, "실패 : ${t.localizedMessage}")
//                request_list.clear()
//            }
//        })

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
                        request_list.addAll(response.body()!!)
                        setPartyRecyclerView()
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
                        request_list.clear()
                        request_list.addAll(response.body()!!)
                        setFriendRecyclerView()
                    } else {
                        Log.d(TAG, "getFriend 실패 : ${response.errorBody().toString()}")
                        request_list.clear()
                    }
                }

                override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                    Log.d(TAG, "getFriend onFailure : ${t.localizedMessage}")
                    request_list.clear()
                }
            })
        }

    }

    private fun addFriend(email: String, friend_email: String) {

        request_list.forEach {
            if (it.email == friend_email) {
                request_list.remove(it)
                friend_list.add(it)
            }
        }

//        requestedPartyListAdapter.setData(request_list as List<Friend>)
        requestedPartyListAdapter.notifyData(request_list as List<Friend>)

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

        var remove_index: Int? = null

        request_list.forEachIndexed { index, it ->
            if (it.email == request_email) {
                remove_index = index
                return@forEachIndexed
            }
        }

        remove_index?.let {
            request_list.removeAt(it)

//            requestedPartyListAdapter.setData(request_list as List<Friend>)
            requestedPartyListAdapter.notifyData(request_list as List<Friend>)
        }

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

        val call: Call<String> = server.delRequestedParty(email, friend_email)
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
                    friend_list.forEach {
                        if (it.email == friend_email)
                            friend_list.remove(it)
                    }

//                   requestedPartyListAdapter.setData(request_list as List<Friend>)
                    friendListAdapter.notifyData(friend_list as List<Friend>)
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
                request_list
            ) { it, flag ->
                val email = FirebaseAuth.getInstance().currentUser?.email
                val request_email = it.email

                if (flag) {
                    request_email?.let {
                        addFriend(email!!, it)
                        removeRequestedParty(email!!, it)
                    }
                } else {
                    request_email?.let {
                        removeRequestedParty(email!!, it)
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
                friend_list
            ) { it, flag ->
                val email = FirebaseAuth.getInstance().currentUser?.email
                val friend_email = it.email

                if (flag) {
                    // TODO it.email에게 메시지 보내기
                    // sendMessage()
                } else {
                    // TODO 친구 삭제하기
                    friend_email?.let {
                        removeFriend(email!!, friend_email)
                    }
                }

            }

        friendListAdapter.setHasStableIds(true)
        binding.rvShowFriends.adapter = friendListAdapter

    }

    private fun initViews() {
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            if (binding.swipeRefreshLayout.isRefreshing) {
//                Log.d(TAG, "swipeRefreshing Now")
//                Snackbar.make(binding.rvRequestedParty, "Refresh Success", Snackbar.LENGTH_SHORT).show()
//                binding.swipeRefreshLayout.isRefreshing = false
//            }
//        }

//        binding.rvRequestedParty.postDelayed(Runnable{
//            Snackbar.make(binding.rvRequestedParty, "Refresh Success", Snackbar.LENGTH_SHORT).show()
//        }, 5000)


    }

}