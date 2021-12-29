package com.naram.party_project

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.os.Bundle as Bundle
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.base.BaseActivity
import com.naram.party_project.base.BaseFragment

import com.naram.party_project.databinding.FragmentMyprofileBinding
import com.naram.party_project.util.Const.Companion.MODIFY_USER_PROFILE
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MyProfileFragment : BaseFragment<FragmentMyprofileBinding>() {

    private val TAG = "MyProfile"
    private lateinit var mainActivity: MainActivity

    // user detail profile
    private lateinit var TendencyTextViewList: List<TextView>
    private lateinit var GameNameTextViewList: List<TextView>

    override fun getFragmentBinding() = FragmentMyprofileBinding.inflate(layoutInflater)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        getRoom()
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        if(MODIFY_USER_PROFILE) {
            getRoom()
            MODIFY_USER_PROFILE = false
        }
    }

//    private fun signOut() {
//        val auth = FirebaseAuth.getInstance()
//
//        Thread(kotlinx.coroutines.Runnable {
//            db.userDAO().deleteAll()
//        }).start()
//
//        auth.signOut()
//
//        activity?.finish()
//        startActivity(Intent(activity, SigninActivity::class.java))
//    }

    private fun initViews() {
        TendencyTextViewList = listOf(
            binding.tvGameTendencyWantWin,
            binding.tvGameTendencyWinOrlose,
            binding.tvGameTendencyOnlyFun,
            binding.tvGameTendencyOnlyWin,
            binding.tvGameTendencyVoiceOk,
            binding.tvGameTendencyVoiceNo,
            binding.tvGameTendencyOnlyWomen,
            binding.tvGameTendencyOnlyMen,
            binding.tvGameTendencyWomenOrmen
        )

        GameNameTextViewList = listOf(
            binding.tvGameNamesLOL,
            binding.tvGameNamesOverWatch,
            binding.tvGameNamesBattleGround,
            binding.tvGameNamesSuddenAttack,
            binding.tvGameNamesFIFAOnline4,
            binding.tvGameNamesLostArk,
            binding.tvGameNamesMapleStory,
            binding.tvGameNamesCyphers,
            binding.tvGameNamesStarCraft,
            binding.tvGameNamesDungeonandFighter
        )

//        binding.btnModifyUserInfo.setOnClickListener {
//            startActivity(Intent(requireContext(), ModifyProfileActivity::class.java))
//        }
    }

    private fun getRoom() {
        Thread(Runnable {
            db.userDAO().getUserInfo().forEach {
                mainActivity.runOnUiThread {
                    it.picture?.let {
                        uploadImageFromCloud(it)
                    }

                    binding.tvUserNickName.text = it.user_name
                    it.game_name?.let {
                        binding.tvGameUserName.text = it
                    }

                    it.self_pr?.let {
                        binding.tvRealSelfPR.text = it
                        binding.tvRealSelfPR.visibility = View.VISIBLE
                    }

                }

                db.tendencyDAO().getTendencyInfo().forEach {
                    mainActivity.runOnUiThread {

                        val tendency = listOf(
                            it.purpose,
                            it.voice,
                            it.preferred_gender,
                            it.game_mode
                        )

                        // Game Tendency
                        val TendencyList = mutableListOf<Games>().apply {
                            TendencyTextViewList.forEach { textView ->
                                this.add(Games(textView, false))
                                tendency.forEach { s ->
                                    if (textView.text.toString() == s) {
                                        this.removeLast()
                                        this.add(Games(textView, true))
                                    }
                                }
                            }
                        }

                        setTextView(TendencyList)
                    }
                }

                db.gameDAO().getGameInfo().forEach {
                    mainActivity.runOnUiThread {

                        val gamenames = listOf(
                            it.game0,
                            it.game1,
                            it.game2,
                            it.game3,
                            it.game4,
                            it.game5,
                            it.game6,
                            it.game7,
                            it.game8,
                            it.game9
                        )

                        // Game Names
                        val GameList = mutableListOf<Games>().apply {
                            GameNameTextViewList.forEachIndexed { index, textView ->
                                if (gamenames[index] == 1) this.add(Games(textView, true))
                                else this.add(Games(textView, false))
                            }
                        }

                        setTextView(GameList)
                    }
                }
            }
        }).start()
    }

    private fun uploadImageFromCloud(path: String) {
        Log.d(TAG, "uploadImageFromCloud")

        val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef = storageRef.child(path)

        imagesRef.downloadUrl.addOnCompleteListener { task ->
            task.addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.loading_image)
                    .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                    .into(binding.ivUserPicture)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setTextView(list: List<Games>) {
        list.forEach {
            if (it.flag) {
                it.textView.setTextColor(resources.getColor(R.color.white, null))
                it.textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_activated, null))
            } else {
                it.textView.setTextColor(resources.getColor(R.color.color_inactivated_blue, null))
                it.textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_inactivated))
            }
        }
    }

}
