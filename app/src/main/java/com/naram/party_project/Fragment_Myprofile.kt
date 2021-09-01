package com.naram.party_project

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.os.Bundle as Bundle
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class Fragment_Myprofile : Fragment() {

    private val TAG = "MyProfile_Fragment"
    lateinit var mainActivity: MainActivity

    private lateinit var db : AppDatabase

    private lateinit var currentPhotoPath: String

    // user simple profile
    private lateinit var iv_userPicture : ImageView
    private lateinit var tv_userNickName: TextView
    private lateinit var tv_gameUserName: TextView
    private lateinit var tv_realSelfPR: TextView
    private lateinit var tv_realKindness: TextView
    private lateinit var tv_estimatedKindness: TextView

    private lateinit var TendencyTextViewList: List<TextView>

    private lateinit var GameNameTextViewList: List<TextView>

    // modify button
    private lateinit var btn_modifyUserInfo: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_myprofile, container, false)

        createDB()
        getRoom()
        initViews(view)

        btn_modifyUserInfo.setOnClickListener {
            // TODO 버튼 클릭 시 정보 수정 화면으로 이동
        }

        return view

    }

    private fun createDB() {
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "userDB"
        )
            .build()
    }

    private fun getRoom() {
        Thread(Runnable {
            db.userDAO().getUserInfo().forEach {
                mainActivity.runOnUiThread {
                    if (it.picture?.isNotEmpty() == true)
                        it.picture?.let { s ->
                            uploadImageFromCloud(s)
//                            db.userDAO().updatePicture(it.email, string_uri)
                        }
                    else {
                        iv_userPicture.setImageDrawable(resources.getDrawable(R.drawable.app_logo))
                        iv_userPicture.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    }
                    tv_userNickName.text = it.user_name
                    tv_gameUserName.text = it.game_name
                    tv_realSelfPR.text = it.self_pr

                    val tendency = listOf(
                        it.tendency0,
                        it.tendency1,
                        it.tendency2,
                        it.tendency3
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
                            this.add(Games(textView, gamenames[index]))
                        }
                    }

                    setTextView(TendencyList)
                    setTextView(GameList)
                }
            }
        }).start()
    }

    private fun initViews(view : View) {

        // Simple User Info
        iv_userPicture = view.findViewById(R.id.iv_userPicture)
        tv_userNickName = view.findViewById(R.id.tv_userNickName)
        tv_gameUserName = view.findViewById(R.id.tv_gameUserName)
        tv_realSelfPR = view.findViewById(R.id.tv_realSelfPR)

        // Detail User Info
        TendencyTextViewList = listOf(
            view.findViewById(R.id.tv_gameTendency_wantWin),
            view.findViewById(R.id.tv_gameTendency_winOrlose),
            view.findViewById(R.id.tv_gameTendency_onlyFun),
            view.findViewById(R.id.tv_gameTendency_onlyWin),
            view.findViewById(R.id.tv_gameTendency_voiceOk),
            view.findViewById(R.id.tv_gameTendency_voiceNo),
            view.findViewById(R.id.tv_gameTendency_onlyWomen),
            view.findViewById(R.id.tv_gameTendency_onlyMen),
            view.findViewById(R.id.tv_gameTendency_womenOrmen)
        )

        GameNameTextViewList = listOf (
            view.findViewById(R.id.tv_gameNames_LOL),
            view.findViewById(R.id.tv_gameNames_OverWatch),
            view.findViewById(R.id.tv_gameNames_BattleGround),
            view.findViewById(R.id.tv_gameNames_SuddenAttack),
            view.findViewById(R.id.tv_gameNames_FIFAOnline4),
            view.findViewById(R.id.tv_gameNames_LostArk),
            view.findViewById(R.id.tv_gameNames_MapleStory),
            view.findViewById(R.id.tv_gameNames_Cyphers),
            view.findViewById(R.id.tv_gameNames_StarCraft),
            view.findViewById(R.id.tv_gameNames_DungeonandFighter)
        )

        btn_modifyUserInfo = view.findViewById(R.id.btn_modifyUserInfo)

    }

    private fun uploadImageFromCloud(path: String) {
        val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef = storageRef.child(path)
//
//        val localFile = File.createTempFile("images", "jpg")
//        var uri : Uri? = null
//
//        imagesRef.getFile(localFile).addOnSuccessListener {
//            Log.d(TAG, "성공하긴 했나요")
//            uri = localFile.toUri()
//        }.addOnFailureListener {
//            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
//        }
//
//        return uri!!

        val flag = uploadImageFromURI(imagesRef)

        if (!flag) {
            uploadImageFromDownload(imagesRef)
        }

    }

    private fun uploadImageFromURI(Ref: StorageReference): Boolean {
        val result = Ref.downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .into(iv_userPicture)
        }

        return result.isSuccessful

    }

    private fun uploadImageFromDownload(Ref: StorageReference) {
        val ONE_MEGABYTE: Long = 1024 * 1024
        Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            val options = BitmapFactory.Options();
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
            iv_userPicture.setImageBitmap(bitmap)
        }.addOnFailureListener {
            iv_userPicture.setImageDrawable(resources.getDrawable(R.drawable.app_logo))
            iv_userPicture.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    private fun setTextView(list: List<Games>) {
        list.forEach {
            if (it.flag) {
                it.textView.setTextColor(resources.getColor(R.color.white))
                it.textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_activated))
            } else {
                it.textView.setTextColor(resources.getColor(R.color.color_inactivated_blue))
                it.textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_inactivated))
            }
        }
    }

}