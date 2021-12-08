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

import com.naram.party_project.databinding.FragmentMyprofileBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MyProfileFragment : Fragment() {

    private val TAG = "MyProfile"
    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private var _binding: FragmentMyprofileBinding? = null

    private val binding get() = _binding!!

    private var modifyFlag = false

    private var currentPhotoPath: String? = null

    // user detail profile
    private lateinit var TendencyTextViewList: List<TextView>
    private lateinit var GameNameTextViewList: List<TextView>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyprofileBinding.inflate(inflater, container, false)
        val view = binding.root

        createDB()
        initViews()
//        signOut()
        getRoom()

        binding.btnModifyUserInfo.setOnClickListener {
            // TODO 버튼 클릭 시 정보 수정 화면으로 이동
            modifyFlag = true
            mainActivity.changeFragment()
        }

        return view

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        if(FirebaseAuth.getInstance().currentUser?.email != null && currentPhotoPath != null) {
//            updateRoom()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        if(modifyFlag) {
            getRoom()
            modifyFlag = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentPhotoPath = null
        _binding = null
    }

    private fun createDB() {
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "userDB"
        )
            .build()
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
    }

    private fun getRoom() {
        Thread(Runnable {
            db.userDAO().getUserInfo().forEach {
                mainActivity.runOnUiThread {
                    if(it.picture_uri == null && currentPhotoPath == null) {
                        it.picture?.let { path ->
                            currentPhotoPath = uploadImageFromCloud(path)
                        }
                    } else if(it.picture_uri != null) {
                        Glide.with(this)
                            .load(it.picture_uri)
                            .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                            .into(binding.ivUserPicture)
                    } else if(currentPhotoPath != null) {
                        Glide.with(this)
                            .load(currentPhotoPath)
                            .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                            .into(binding.ivUserPicture)
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

    private fun updateRoom() {
        Thread(Runnable {
            db.userDAO().updatePicture(FirebaseAuth.getInstance().currentUser!!.email!!, currentPhotoPath!!)
        }).start()
    }

    private fun uploadImageFromCloud(path: String) : String {
        val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef = storageRef.child(path)

        Log.d(TAG, "imagesRef is $imagesRef")

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timeStamp}_user_profile"
        val localFile = File.createTempFile(fileName, ".jpg", requireContext().filesDir)

        imagesRef.getFile(localFile).addOnSuccessListener {
            Glide.with(this)
                .load(localFile)
                .placeholder(R.drawable.app_logo_reverse)
                .error(R.drawable.app_logo_reverse)
                .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                .into(binding.ivUserPicture)
        }.addOnFailureListener {
            val flag = uploadImageFromURI(imagesRef)

            if (!flag)
                uploadImageFromDownload(imagesRef)

            Log.d(TAG, "uploadImageFromCloud에서 오류 발생")
            Log.d(TAG, it.toString())
        }

        return localFile.absolutePath

    }

    private fun uploadImageFromURI(Ref: StorageReference): Boolean {
        val result = Ref.downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                .into(binding.ivUserPicture)
        }.addOnFailureListener {
            Log.d(TAG, "uploadImageFromURI에서 오류 발생")
            Log.d(TAG, it.toString())
        }

        return result.isSuccessful
    }

    private fun uploadImageFromDownload(Ref: StorageReference) {
        val ONE_MEGABYTE: Long = 1024 * 1024
        Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            val options = BitmapFactory.Options();
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
            binding.ivUserPicture.setImageBitmap(bitmap)
        }.addOnFailureListener {
            binding.ivUserPicture.setImageDrawable(resources.getDrawable(R.drawable.app_logo, resources.newTheme()))

            Toast.makeText(requireContext(), "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()

            Log.d(TAG, "uploadImageFromDownload에서 오류 발생")
            Log.d(TAG, it.toString())
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
