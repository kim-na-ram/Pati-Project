package com.naram.party_project

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.naram.party_project.adapter.UserListAdapter
import com.naram.party_project.base.BaseViewDataFragment
import com.naram.party_project.callback.PartyFirebase
import com.naram.party_project.callback.User
import com.naram.party_project.callback.UserFirebase
import com.naram.party_project.databinding.FragmentSearchpartyBinding
import com.naram.party_project.dialog.ShowProfileDialog
import com.naram.party_project.util.Const
import com.naram.party_project.util.Const.Companion.FIREBASE_FRIEND
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_0_LOL
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_1_OVER_WATCH
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_2_BATTLE_GROUND
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_3_SUDDEN_ATTACK
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_4_FIFA_ONLINE_4
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_5_LOST_ARK
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_6_MAPLE_STORY
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_7_CYPHERS
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_8_STAR_CRAFT
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME_9_DUNGEON_AND_FIGHTER
import com.naram.party_project.util.Const.Companion.FIREBASE_TABLE_NAMES
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_EMAIL
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GAME_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GENDER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_SELF_PR
import com.naram.party_project.util.Const.Companion.UPDATE_USER_LIST
import com.naram.party_project.viewmodel.SearchPartyViewModel
import kotlinx.coroutines.runBlocking

class SearchPartyFragment : BaseViewDataFragment<FragmentSearchpartyBinding>(
    R.layout.fragment_searchparty
) {

    private val TAG = "Search Party"

    private lateinit var mainActivity: MainActivity

    private var userList = mutableListOf<PartyFirebase>()
    private val friendList = mutableListOf<String>()

    private lateinit var searchPartyViewModel: SearchPartyViewModel

    private lateinit var mDatabaseReference : DatabaseReference

//    private lateinit var transaction: FragmentTransaction

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun init() {

        setViewModel()
        setRecyclerView()
        setPartyUserListFromFirebase()
        showSampleData(true)

    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()

        if (UPDATE_USER_LIST) {
            showSampleData(true)
            Log.d(TAG, "UPDATE USER LIST")
            searchPartyViewModel.currentList.value.let {
                userList.clear()
                setPartyUserListFromFirebase()
            }
        }

        Log.d(TAG, "onResume")

    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        userList.clear()

        Log.d(TAG, "onDestroyView")
    }

    private fun setViewModel() {
        searchPartyViewModel = ViewModelProvider(this).get(SearchPartyViewModel::class.java)
        binding.viewModel = searchPartyViewModel

        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setRecyclerView() {
        val adapter = UserListAdapter {
//            appearFragment(it)
            val dlg = ShowProfileDialog(requireContext(), it.uid, it.name)
            dlg.showProfileDialog()
        }
        adapter.setHasStableIds(true)
        binding.rvPartyUserList.adapter = adapter
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.slPartyShimmer.startShimmer()
            binding.slPartyShimmer.visibility = View.VISIBLE
            binding.rvPartyUserList.visibility = View.INVISIBLE
        } else {
            binding.slPartyShimmer.stopShimmer()
            binding.slPartyShimmer.visibility = View.GONE
            binding.rvPartyUserList.visibility = View.VISIBLE
        }
    }

    private fun setPartyUserListFromFirebase() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email
        var gender: String? = null

        runBlocking {
            val thread = Thread(Runnable {
                val stringGender = mainActivity.getDB().tendencyDAO().getTendencyGenderInfo(email!!)
                gender = when (stringGender) {
                    "여성 Only" -> "F"
                    "남성 Only" -> "M"
                    else -> "ALL"
                }
            })

            thread.start()
            thread.join()

            mDatabaseReference = FirebaseDatabase.getInstance().reference

            mDatabaseReference.child(FIREBASE_FRIEND).child(uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            friendList.add(it.key.toString())
                        }

                        showUserList(uid, gender!!)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, error.message)
                    }

                })

//            mDatabaseReference.get()
//                .addOnSuccessListener { dataSnapshot ->
//                    dataSnapshot.children.forEach {
//                        if (!it.key.equals(uid)
//                            && !FIREBASE_TABLE_NAMES.contains(it.key)
//                        ) {
//
//                            val mUserDB = it.child(Const.FIREBASE_USER)
//
//                            if (mUserDB.child("gender").value.toString() == gender) {
//                                addFirebaseList(it)
//                            } else {
//                                if (gender.equals("ALL")) {
//                                    addFirebaseList(it)
//                                }
//                            }
//                        }
//                    }
//
//                    searchPartyViewModel.updateList(firebaseList)
//                    if (binding.slPartyShimmer.visibility == View.VISIBLE)
//                        showSampleData(false)
//
//                }.addOnFailureListener {
//                    Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//                }
        }

    }

    private fun showUserList(uid : String, gender : String) {
        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if (!it.key.equals(uid)
                        && !FIREBASE_TABLE_NAMES.contains(it.key)
                        && !friendList.contains(it.key)
                    ) {

                        val mUserDB = it.child(FIREBASE_USER)

                        if (mUserDB.child("gender").value.toString() == gender) {
                            addFirebaseList(it)
                        } else {
                            if (gender.equals("ALL")) {
                                addFirebaseList(it)
                            }
                        }

                    }
                }

                searchPartyViewModel.updateList(userList)
                if (binding.slPartyShimmer.visibility == View.VISIBLE)
                    showSampleData(false)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
            }

        })
    }

    private fun addFirebaseList(dataSnapshot: DataSnapshot) {
        val mUserDB = dataSnapshot.child(FIREBASE_USER)
        val mGameDB = dataSnapshot.child(FIREBASE_GAME)

        userList.add(
            PartyFirebase(
                dataSnapshot.key.toString(),
                mUserDB.child(FIREBASE_USER_EMAIL).value.toString(),
                mUserDB.child(FIREBASE_USER_NAME).value.toString(),
                mUserDB.child(FIREBASE_USER_GENDER).value.toString(),
                if (mUserDB.hasChild(FIREBASE_USER_GAME_NAME)) mUserDB.child(FIREBASE_USER_GAME_NAME).value.toString() else null,
                if (mUserDB.hasChild(FIREBASE_USER_SELF_PR)) mUserDB.child(FIREBASE_USER_SELF_PR).value.toString() else null,
                if (mUserDB.hasChild(FIREBASE_USER_PICTURE)) mUserDB.child(FIREBASE_USER_PICTURE).value.toString() else null,
                mGameDB.child(FIREBASE_GAME_0_LOL).value.toString(),
                mGameDB.child(FIREBASE_GAME_1_OVER_WATCH).value.toString(),
                mGameDB.child(FIREBASE_GAME_2_BATTLE_GROUND).value.toString(),
                mGameDB.child(FIREBASE_GAME_3_SUDDEN_ATTACK).value.toString(),
                mGameDB.child(FIREBASE_GAME_4_FIFA_ONLINE_4).value.toString(),
                mGameDB.child(FIREBASE_GAME_5_LOST_ARK).value.toString(),
                mGameDB.child(FIREBASE_GAME_6_MAPLE_STORY).value.toString(),
                mGameDB.child(FIREBASE_GAME_7_CYPHERS).value.toString(),
                mGameDB.child(FIREBASE_GAME_8_STAR_CRAFT).value.toString(),
                mGameDB.child(FIREBASE_GAME_9_DUNGEON_AND_FIGHTER).value.toString()
            )
        )
    }

//    private fun appearFragment(party: PartyFirebase) {
//        val bundle = Bundle()
//        bundle.putString("uid", party.uid)
//        bundle.putString("name", party.name)
//
//        binding.flShowPartyProfile.visibility = View.VISIBLE
//
//        val showProfileFragment = ShowProfileFragment()
//        showProfileFragment.arguments = bundle
//        transaction = childFragmentManager.beginTransaction()
//        transaction.replace(R.id.flShowPartyProfile, showProfileFragment)
//            .addToBackStack(null)
//            .commit()
//    }
//
//    fun disappearFragment() {
//        transaction.detach(ShowProfileFragment())
//        binding.flShowPartyProfile.visibility = View.GONE
//    }

}