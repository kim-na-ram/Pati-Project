package com.naram.party_project

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.naram.party_project.callback.Party
import com.naram.party_project.databinding.FragmentSearchpartyBinding
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.Runnable
import java.util.*
import kotlin.concurrent.timer

class Fragment_Searchparty : Fragment() {

    private val TAG = "Searchparty"

    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private val email = FirebaseAuth.getInstance().currentUser?.email
    private var list: List<Party>? = null

    private var _binding: FragmentSearchpartyBinding? = null
    private val binding get() = _binding!!

    lateinit var userListAdapter: UserListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchpartyBinding.inflate(inflater, container, false)
        val view = binding.root

        Log.d(TAG, "onCreateView")

        setRecyclerView()
        loadSampleData()

        return view
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")

    }

    override fun onPause() {
        super.onPause()

        mainActivity.disappearFragment()

        Log.d(TAG, "onPause")
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        Log.d(TAG, "onViewCreated")
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun loadSampleData() {

        showSampleData(isLoading = true)

        timer(period = 1000) {
            if (mainActivity.checkFlag()) {
                list = mainActivity.getList()
                userListAdapter.submitList(list!!)

                this.cancel()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if(mainActivity.checkFlag())
                showSampleData(false)
        }, 3000)


    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.slShowFirst.startShimmer()
            binding.slShowFirst.visibility = View.VISIBLE
        } else {
            binding.slShowFirst.stopShimmer()
            binding.rvPartyUserList.visibility = View.VISIBLE
            binding.slShowFirst.visibility = View.INVISIBLE
        }
    }

    private fun setRecyclerView() {

        userListAdapter = UserListAdapter(this.requireContext(), mutableListOf()) {
            if (mainActivity.getProfile() == null)
                mainActivity.getPartyUserProfile(it.email)
            else {
                mainActivity.disappearFragment()
                mainActivity.getPartyUserProfile(it.email)
            }
        }
        userListAdapter.setHasStableIds(true)
        binding.rvPartyUserList.adapter = userListAdapter

    }

}