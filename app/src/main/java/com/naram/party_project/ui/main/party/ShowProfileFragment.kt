package com.naram.party_project.ui.main.party

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.ui.chat.ChattingActivity
import com.naram.party_project.R
import com.naram.party_project.ui.base.BaseFragment
import com.naram.party_project.data.remote.model.UserFirebase
import com.naram.party_project.databinding.FragmentShowprofileBinding
import com.naram.party_project.ui.main.MainActivity
import com.naram.party_project.util.Const
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
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_EMAIL
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GAME_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GENDER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_SELF_PR
import com.nex3z.flowlayout.FlowLayout
import kotlinx.coroutines.runBlocking

class ShowProfileFragment : BaseFragment<FragmentShowprofileBinding>() {

    private val TAG = "Showprofile"

    private lateinit var mainActivity: MainActivity

    private lateinit var uid: String
    private lateinit var name: String
    private lateinit var user: UserFirebase

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun getFragmentBinding() = FragmentShowprofileBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

        uid = arguments?.getString("uid")!!
        name = arguments?.getString("name")!!

        initViews()
        uid?.let {
            getUserProfile(uid)
        }
//        setUserProfile()
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")
    }

    private fun initViews() {

        binding.btnSendMessage.setOnClickListener {
            val intent = Intent(activity, ChattingActivity::class.java)
            intent.putExtra("myUID", FirebaseAuth.getInstance().uid)
            intent.putExtra("othersUID", uid)
            intent.putExtra("chatRoomName", name)

            startActivity(intent)
        }

        binding.btnRequestParty.setOnClickListener {
            // TODO ?????? ?????? ?????????
            runBlocking {
                requestParty()
            }
        }

    }

    private fun requestParty() {

//        val retrofit = RetrofitClient.getInstance()
//
//        val server = retrofit.create(UserAPI::class.java)
//
//        val request_email = FirebaseAuth.getInstance().currentUser?.email
////        val receive_email = mainActivity.getProfile()?.email
//
//        receive_email?.let {
//
//            val call : Call<String> = server.putRequestedParty(request_email!!, receive_email)
//            call.enqueue(object : Callback<String> {
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d(TAG,"?????? : "+t.localizedMessage)
//                }
//
//                override fun onResponse(
//                    call: Call<String>,
//                    response: Response<String>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d(TAG, "?????? : ${response.body().toString()}")
//                        Toast.makeText(
//                            requireContext(),
//                            "\'${binding.tvPartyUserName.text}\'????????? ?????? ????????? ???????????????!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        Log.d(TAG, "?????? : ${response.errorBody().toString()}")
//                    }
//                }
//            })
//
//        }

    }

    private fun getUserProfile(uid : String) {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mUserDB = snapshot.child(FIREBASE_USER)
                    val mTendencyDB = snapshot.child(FIREBASE_TENDENCY)
                    val mGameDB = snapshot.child(FIREBASE_GAME)

                    user = UserFirebase(
                        uid,
                        mUserDB.child(FIREBASE_USER_EMAIL).value.toString(),
                        name,
                        mUserDB.child(FIREBASE_USER_GENDER).value.toString(),
                        mUserDB.child(FIREBASE_USER_GAME_NAME).value.toString(),
                        mUserDB.child(FIREBASE_USER_SELF_PR).value.toString(),
                        mUserDB.child(FIREBASE_USER_PICTURE).value.toString(),
                        mGameDB.child(FIREBASE_GAME_0_LOL).value.toString(),
                        mGameDB.child(FIREBASE_GAME_1_OVER_WATCH).value.toString(),
                        mGameDB.child(FIREBASE_GAME_2_BATTLE_GROUND).value.toString(),
                        mGameDB.child(FIREBASE_GAME_3_SUDDEN_ATTACK).value.toString(),
                        mGameDB.child(FIREBASE_GAME_4_FIFA_ONLINE_4).value.toString(),
                        mGameDB.child(FIREBASE_GAME_5_LOST_ARK).value.toString(),
                        mGameDB.child(FIREBASE_GAME_6_MAPLE_STORY).value.toString(),
                        mGameDB.child(FIREBASE_GAME_7_CYPHERS).value.toString(),
                        mGameDB.child(FIREBASE_GAME_8_STAR_CRAFT).value.toString(),
                        mGameDB.child(FIREBASE_GAME_9_DUNGEON_AND_FIGHTER).value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_PURPOSE).value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_VOICE).value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN)
                            .value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_PREFERRED_GENDER_MEN)
                            .value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_GAME_MODE).value.toString()
                    )

                    Log.d(TAG, "$user")

                    user?.let {
                        setUserProfile()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message)
                }

            })
    }

    private fun setUserProfile() {

        Log.e(TAG, "user : $user")

        user?.let { user ->
            val name = user.user_name
            binding.tvPartyUserName.text = name

            user.self_pr?.let {
                binding.tvPartyLeftQuote.visibility = View.VISIBLE
                binding.tvPartyRightQuote.visibility = View.VISIBLE
                binding.tvPartyUserPR.visibility = View.VISIBLE
                binding.tvPartyUserPR.text = it
            }

            user.picture?.let {
                uploadImageFromCloud(it)
            }

            user.purpose.toInt()?.let {
                var str = if (it == 1) "????????????" else "???????????? x"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            user.voice.toInt()?.let {
                var str = if (it == 1) "???????????? O" else "???????????? X"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            user.game_mode.toInt()?.let {
                var str = if (it == 1) "??????" else "??????"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            val men = user.men.toInt()
            val women = user.women.toInt()
            men?.let { m ->
                var str: String? = null
                women?.let { w ->
                    if (m == 1 && w == 1) str = "???????????? X"
                    else if (m == 1 && w == 0) str = "?????? Only"
                    else if (m == 0 && w == 1) str = "?????? Only"
                    makeTextView(str!!, binding.flPartyShowTendency)
                }
            }

            if (user.game0?.toInt() == 1) binding.tvPartyGame0.visibility = View.VISIBLE
            if (user.game1?.toInt() == 1) binding.tvPartyGame1.visibility = View.VISIBLE
            if (user.game2?.toInt() == 1) binding.tvPartyGame2.visibility = View.VISIBLE
            if (user.game3?.toInt() == 1) binding.tvPartyGame3.visibility = View.VISIBLE
            if (user.game4?.toInt() == 1) binding.tvPartyGame4.visibility = View.VISIBLE
            if (user.game5?.toInt() == 1) binding.tvPartyGame5.visibility = View.VISIBLE
            if (user.game6?.toInt() == 1) binding.tvPartyGame6.visibility = View.VISIBLE
            if (user.game7?.toInt() == 1) binding.tvPartyGame7.visibility = View.VISIBLE
            if (user.game8?.toInt() == 1) binding.tvPartyGame8.visibility = View.VISIBLE
            if (user.game9?.toInt() == 1) binding.tvPartyGame9.visibility = View.VISIBLE
        }
    }

    private fun uploadImageFromCloud(path: String) {
        val imagesRef = Firebase.storage.reference.child(path)

        imagesRef.downloadUrl.addOnCompleteListener { task ->
            task.addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .override(
                        binding.ivPartyUserPicture.width,
                        binding.ivPartyUserPicture.height
                    )
                    .placeholder(resources.getDrawable(R.drawable.loading_image, null))
                    .into(binding.ivPartyUserPicture)
            }.addOnFailureListener {
                Toast.makeText(
                    binding.root.context,
                    "????????? ??????????????? ????????? ??????????????????.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun makeTextView(text: String, layout: FlowLayout) {
        val gameTextView = TextView(this.requireContext())
        gameTextView.text = text
        gameTextView.setTextColor(resources.getColor(R.color.color_white, null))
        gameTextView.background = resources.getDrawable(R.drawable.textview_rounded_activated, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gameTextView.typeface = resources.getFont(R.font.nanumsquarebold)
        }
        gameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.toFloat())
        gameTextView.setPadding(
            3.dpToPixels(requireContext()),
            1.dpToPixels(requireContext()),
            3.dpToPixels(requireContext()),
            1.dpToPixels(requireContext())
        )

        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginEnd = 5.dpToPixels(requireContext())
            bottomMargin = 5.dpToPixels(requireContext())

            gameTextView.layoutParams = this
        }

        layout.addView(gameTextView)
    }

    fun Int.dpToPixels(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

}