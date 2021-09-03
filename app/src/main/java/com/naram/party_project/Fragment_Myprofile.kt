package com.naram.party_project

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.os.Bundle as Bundle
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

import com.naram.party_project.databinding.FragmentMyprofileBinding

class Fragment_Myprofile : Fragment() {

    private val TAG = "MyProfile_Fragment"
    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private var _binding: FragmentMyprofileBinding? = null

    private val binding get() = _binding!!

//    private lateinit var currentPhotoPath: String

    // user detail profile
    private lateinit var TendencyTextViewList: List<TextView>
    private lateinit var GameNameTextViewList: List<TextView>

    private val Fragment_ModifyUserInfo by lazy {
        Fragment_Modifyprofile()
    }

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
        getRoom()

        binding.btnModifyUserInfo.setOnClickListener {
            // TODO 버튼 클릭 시 정보 수정 화면으로 이동
            val fg = Fragment_ModifyUserInfo.newInstance()
            setChildFragment(fg)

        }

        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
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

                    binding.ivUserPicture.setImageDrawable(resources.getDrawable(R.drawable.app_logo))
                    binding.ivUserPicture.scaleType = ImageView.ScaleType.CENTER_INSIDE

                    it.picture?.let {
                        uploadImageFromCloud(it)
//                            db.userDAO().updatePicture(it.email, string_uri)
                    }

                    binding.tvUserNickName.text = it.user_name
                    it.game_name?.let {
                        binding.tvGameUserName.text = it
                    }

                    it.self_pr?.let {
                        binding.tvRealSelfPR.text = it
                        binding.tvRealSelfPR.visibility = View.VISIBLE
                    }

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
                .into(binding.ivUserPicture)
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
            binding.ivUserPicture.setImageDrawable(resources.getDrawable(R.drawable.app_logo))
            binding.ivUserPicture.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    private fun setChildFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()

        binding.btnModifyUserInfo.visibility = View.GONE

        fragmentTransaction.replace(R.id.fl_modify_userProfile, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

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