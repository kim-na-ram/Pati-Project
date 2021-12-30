package com.naram.party_project.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.R
import com.naram.party_project.callback.UserFirebase
import com.naram.party_project.databinding.DialogShowprofileBinding
import com.naram.party_project.util.Const
import com.naram.party_project.util.Const.Companion.FIREBASE_PARTY
import com.naram.party_project.util.Const.Companion.FIREBASE_USERS
import com.nex3z.flowlayout.FlowLayout

class ShowProfileDialog(
    private val context: Context,
    private val othersUID: String,
    private val othersName: String
) {
    private val TAG = "Show Profile"
    private val dlg = Dialog(context)
    private lateinit var binding: DialogShowprofileBinding
    private lateinit var user: UserFirebase

    fun showProfileDialog() {
        showProfile()
        getUserProfile()
    }

    private fun showProfile() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_showprofile,
            null,
            false
        )

        binding.dialog = this
        dlg.setContentView(binding.root)
        dlg.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dlg.show()
    }

    private fun getUserProfile() {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabaseReference.child(FIREBASE_USERS).child(othersUID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mUserDB = snapshot.child(Const.FIREBASE_USER)
                    val mTendencyDB = snapshot.child(Const.FIREBASE_TENDENCY)
                    val mGameDB = snapshot.child(Const.FIREBASE_GAME)

                    user = UserFirebase(
                        othersUID,
                        mUserDB.child(Const.FIREBASE_USER_EMAIL).value.toString(),
                        othersName,
                        mUserDB.child(Const.FIREBASE_USER_GENDER).value.toString(),
                        if(mUserDB.child(Const.FIREBASE_USER_GAME_NAME).exists()) mUserDB.child(Const.FIREBASE_USER_GAME_NAME).value.toString() else null,
                        if(mUserDB.child(Const.FIREBASE_USER_SELF_PR).exists()) mUserDB.child(Const.FIREBASE_USER_SELF_PR).value.toString() else null,
                        if(mUserDB.child(Const.FIREBASE_USER_PICTURE).exists()) mUserDB.child(Const.FIREBASE_USER_PICTURE).value.toString() else null,
                        mGameDB.child(Const.FIREBASE_GAME_0_LOL).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_1_OVER_WATCH).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_2_BATTLE_GROUND).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_3_SUDDEN_ATTACK).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_4_FIFA_ONLINE_4).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_5_LOST_ARK).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_6_MAPLE_STORY).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_7_CYPHERS).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_8_STAR_CRAFT).value.toString(),
                        mGameDB.child(Const.FIREBASE_GAME_9_DUNGEON_AND_FIGHTER).value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_PURPOSE).value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_VOICE).value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN)
                            .value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_PREFERRED_GENDER_MEN)
                            .value.toString(),
                        mTendencyDB.child(Const.FIREBASE_TENDENCY_GAME_MODE).value.toString()
                    )

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
                var str = if (it == 1) "승리지향" else "승패상관 x"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            user.voice.toInt()?.let {
                var str = if (it == 1) "보이스톡 O" else "보이스톡 X"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            user.game_mode.toInt()?.let {
                var str = if (it == 1) "즐겜" else "빡겜"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            val men = user.men.toInt()
            val women = user.women.toInt()
            men?.let { m ->
                var str: String? = null
                women?.let { w ->
                    if (m == 1 && w == 1) str = "성별상관 X"
                    else if (m == 1 && w == 0) str = "남성 Only"
                    else if (m == 0 && w == 1) str = "여성 Only"
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
                Glide.with(context)
                    .load(it)
                    .override(
                        binding.ivPartyUserPicture.width,
                        binding.ivPartyUserPicture.height
                    )
                    .placeholder(context.resources.getDrawable(R.drawable.loading_image, null))
                    .into(binding.ivPartyUserPicture)
            }.addOnFailureListener {
                Toast.makeText(
                    binding.root.context,
                    "사진을 불러오는데 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun makeTextView(text: String, layout: FlowLayout) {
        val gameTextView = TextView(context)
        gameTextView.text = text
        gameTextView.setTextColor(context.resources.getColor(R.color.color_white, null))
        gameTextView.background = context.resources.getDrawable(R.drawable.textview_rounded_activated, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gameTextView.typeface = context.resources.getFont(R.font.nanumsquarebold)
        }
        gameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.toFloat())
        gameTextView.setPadding(
            3.dpToPixels(context),
            1.dpToPixels(context),
            3.dpToPixels(context),
            1.dpToPixels(context)
        )

        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginEnd = 5.dpToPixels(context)
            bottomMargin = 5.dpToPixels(context)

            gameTextView.layoutParams = this
        }

        layout.addView(gameTextView)
    }

    fun Int.dpToPixels(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                dlg.dismiss()
            }
            R.id.btnRequestParty -> {
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                val myUID = FirebaseAuth.getInstance().uid

                mDatabaseReference.child(FIREBASE_PARTY).child(othersUID).child(myUID!!).setValue("wait")
                    .addOnSuccessListener {
                    Toast.makeText(context, "파티 요청을 보냈습니다!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.d(TAG, it.stackTraceToString())
                }
            }
        }
    }

}
