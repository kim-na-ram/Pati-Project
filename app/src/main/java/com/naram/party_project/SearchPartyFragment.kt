package com.naram.party_project

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.naram.party_project.callback.PartyFirebase
import com.naram.party_project.callback.User
import com.naram.party_project.callback.UserFirebase
import com.naram.party_project.databinding.FragmentSearchpartyBinding
import com.naram.party_project.util.Const
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
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_EMAIL
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GAME_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GENDER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_SELF_PR
import com.naram.party_project.viewmodel.SearchPartyViewModel
import kotlinx.coroutines.runBlocking

class SearchPartyFragment : Fragment() {

    private val TAG = "Searchparty"

    private lateinit var mainActivity: MainActivity

    private var firebaseList = mutableListOf<PartyFirebase>()

    private var _binding: FragmentSearchpartyBinding? = null
    private val binding get() = _binding!!

    lateinit var searchPartyViewModel: SearchPartyViewModel

    lateinit var user : UserFirebase

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

        setViewModel()
        setPartyUserListFromFirebase()
        showSampleData(true)

        return view
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart")
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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun setViewModel() {
        searchPartyViewModel = ViewModelProvider(this).get(SearchPartyViewModel::class.java)
        binding.viewModel = searchPartyViewModel

        binding.lifecycleOwner = viewLifecycleOwner
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

            val mDatabaseReference = FirebaseDatabase.getInstance().reference

            mDatabaseReference.get()
                .addOnSuccessListener { dataSnapshot ->
                    dataSnapshot.children.forEach {
                        if (!it.key.equals(uid)) {

                            Log.d(TAG, "firebase add ${it.key}")

                            val mUserDB = it.child(Const.FIREBASE_USER)

                            if (mUserDB.child("gender").value.toString() == gender) {
                                addFirebaseList(it)
                            } else {
                                if (gender.equals("ALL")) {
                                    addFirebaseList(it)
                                }
                            }
                        }
                    }

                    searchPartyViewModel.updateList(firebaseList)
                    showSampleData(false)

                }.addOnFailureListener {
                    Log.d(TAG, "로그인 실패")
                }
        }

    }

    private fun addFirebaseList(dataSnapshot : DataSnapshot) {
        val mUserDB = dataSnapshot.child(Const.FIREBASE_USER)
        val mGameDB = dataSnapshot.child(Const.FIREBASE_GAME)

//        var dw : Drawable? = null

//        if (mUserDB.child(FIREBASE_USER_BIT_PICTURE).exists()) {
//            val b = Base64.decode(
//                mUserDB.child("bitPicture").value.toString(),
//                Base64.DEFAULT
//            )
//            val bis = ByteArrayInputStream(b)
//            dw = Drawable.createFromStream(bis, "photo")
//        }

        firebaseList.add(
            PartyFirebase(
                dataSnapshot.key.toString(),
                mUserDB.child(FIREBASE_USER_EMAIL).value.toString(),
                mUserDB.child(FIREBASE_USER_NAME).value.toString(),
                mUserDB.child(FIREBASE_USER_GENDER).value.toString(),
                if(mUserDB.hasChild(FIREBASE_USER_GAME_NAME)) mUserDB.child(FIREBASE_USER_GAME_NAME).value.toString() else null,
                if(mUserDB.hasChild(FIREBASE_USER_SELF_PR)) mUserDB.child(FIREBASE_USER_SELF_PR).value.toString() else null,
                if(mUserDB.hasChild(FIREBASE_USER_PICTURE)) mUserDB.child(FIREBASE_USER_PICTURE).value.toString() else null,
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

    fun appearUserProfile(party: PartyFirebase) {
        MainActivity().appearFragment()

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.get()
            .addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { dataSnapshot ->
                    if (dataSnapshot.key.equals(party.uid)) {

                        val mTendencyDB = dataSnapshot.child(Const.FIREBASE_TENDENCY)

                        user = UserFirebase(
                            party.email,
                            party.name,
                            party.gender,
                            party.game_name,
                            party.self_pr,
                            party.picture,
                            party.game0,
                            party.game1,
                            party.game2,
                            party.game3,
                            party.game4,
                            party.game5,
                            party.game6,
                            party.game7,
                            party.game8,
                            party.game9,
                            mTendencyDB.child(Const.FIREBASE_TENDENCY_PURPOSE).toString(),
                            mTendencyDB.child(Const.FIREBASE_TENDENCY_VOICE).toString(),
                            mTendencyDB.child(Const.FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN).toString(),
                            mTendencyDB.child(Const.FIREBASE_TENDENCY_PREFERRED_GENDER_MEN).toString(),
                            mTendencyDB.child(Const.FIREBASE_TENDENCY_GAME_MODE).toString()
                        )

                    }
                }
            }

    }

    fun disappearUserProfile() {
        MainActivity().disappearFragment()
    }

    fun getUserProfile(): UserFirebase? {
        return user
    }

}