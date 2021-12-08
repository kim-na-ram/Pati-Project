package com.naram.party_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import java.io.FileNotFoundException

import com.naram.party_project.util.Const.Companion.ListToMap
import com.naram.party_project.util.Const.Companion.REQUEST_GALLERY
import com.naram.party_project.util.Const.Companion.REQUEST_SUCCESS
import com.naram.party_project.util.Const.Companion.processingTendency
import com.naram.party_project.databinding.FragmentModifyprofileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ModifyProfileFragment : Fragment() {

    private val TAG = "ModifyProfile"

    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private var _binding: FragmentModifyprofileBinding? = null

    private val binding get() = _binding!!

    // user detail profile
    private lateinit var TendencyRadioList: List<RadioButton>
    private lateinit var GameNameTextViewList: List<TextView>

    private val TendencyListToString = mutableListOf<String>()
    private val TendencyListToMap = mutableMapOf<String, String>()
    private var GameNameList: List<Games>? = null
    private val GameListToString = mutableListOf<String>()
    private val GameListToInt = mutableListOf<Int>()

    private var bitmap: Bitmap? = null
    private var picture_path: String? = null
    private var bitPicture: String? = null
    private var currentPhotoPath: String? = null
    private var pictureFlag: Boolean = false

    private lateinit var email: String
    private lateinit var user_name: String
    private var game_name: String? = null
    private var self_pr: String? = null

    fun newInstance(): ModifyProfileFragment {
        return ModifyProfileFragment()
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
        _binding = FragmentModifyprofileBinding.inflate(inflater, container, false)
        val view = binding.root

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user?.let {
            email = user.email!!
        }

        createDB()
        initViews()
        getRoom()
        modifyUserInfo()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy")

        TendencyListToString.clear()
        TendencyListToMap.clear()
        GameNameList = null
        GameListToString.clear()
        GameListToInt.clear()

        bitmap = null
        picture_path = null
        bitPicture = null
        currentPhotoPath = null
        pictureFlag = false

        mainActivity.removeFragment()
    }

//    override fun onPause() {
//        super.onPause()
//
//        mainActivity.removeFragment()
//    }

    private fun createDB() {
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "userDB"
        )
            .build()
    }

    private fun initViews() {

        // Detail User Info
        TendencyRadioList = listOf(
            binding.rbWantWin,
            binding.rbWinOrlose,
            binding.rbFunny,
            binding.rbHard,
            binding.rbVoiceOn,
            binding.rbVoiceOff,
            binding.rbWomen,
            binding.rbMen,
            binding.rbAll
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

        binding.ibRemoveUserPicture.setOnClickListener {
            binding.btnModifyUserPicture.text = "프로필 사진 선택"
            Glide.with(this)
                .load(R.drawable.app_logo_reverse)
                .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                .into(binding.ivUserPicture)
            pictureFlag = false
            picture_path = null
            bitPicture = null
        }

        binding.btnModifyUserPicture.setOnClickListener {
            getUserPermission()
        }

    }

    private fun getRoom() {
        Thread(Runnable {
            db.userDAO().getUserInfo().forEach {
                mainActivity.runOnUiThread {

                    if (it.picture_uri == null && currentPhotoPath == null) {
                        it.picture?.let { path ->
                            currentPhotoPath = uploadImageFromCloud(path)
                        }
                    } else if (it.picture_uri != null) {
                        Glide.with(this)
                            .load(it.picture_uri)
                            .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                            .into(binding.ivUserPicture)
                    } else if (currentPhotoPath != null) {
                        Glide.with(this)
                            .load(currentPhotoPath)
                            .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                            .into(binding.ivUserPicture)
                    }

                    binding.etUserNickName.setText(it.user_name)
                    it.game_name?.let {
                        binding.etGameUserName.setText(it)
                    }

                    it.self_pr?.let {
                        binding.etSelfPR.setText(it)
                    }

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
                    TendencyRadioList.forEach { rb ->
                        tendency.forEach { s ->
                            if (rb.text.toString() == s) {
                                rb.isChecked = true
                            }
                        }
                    }
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
                    GameNameList = mutableListOf<Games>().apply {
                        GameNameTextViewList.forEachIndexed { index, textView ->
                            if (gamenames[index] == 1) this.add(Games(textView, true))
                            else this.add(Games(textView, false))
                        }
                    }

                    setTextView(GameNameList)
                }
            }

        }).start()
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imgArray = stream.toByteArray()

        bitPicture = Base64.encodeToString(imgArray, Base64.DEFAULT)

    }

    private fun uploadImageFromCloud(path: String): String {
        val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef = storageRef.child(path)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timeStamp}_user_profile"
        var localFile = File.createTempFile(fileName, ".jpg", requireContext().filesDir)

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

            localFile = null

            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "uploadImageFromCloud에서 오류 발생")
            Log.d(TAG, it.toString())
        }

        return localFile.absolutePath

    }

    private fun uploadImageFromURI(Ref: StorageReference): Boolean {
        val result = Ref.downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                .into(binding.ivUserPicture)
        }
            .addOnFailureListener {
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
            Glide.with(this)
                .load(bitmap)
                .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                .into(binding.ivUserPicture)
        }.addOnFailureListener {
            Glide.with(this)
                .load(R.drawable.app_logo_reverse)
                .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                .into(binding.ivUserPicture)

            Log.d(TAG, "uploadImageFromDownload에서 오류 발생")
            Log.d(TAG, it.toString())
        }
    }

    private fun uploadImageToCloud(bitmap: Bitmap) {
        val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef = storageRef.child(email).child("profile.jpg")

//        var bitmap: Bitmap
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
//        } else {
//            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//        }
//
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        var uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(
                this@ModifyProfileFragment.requireContext(),
                "사진 업로드에 실패했습니다.",
                Toast.LENGTH_SHORT
            )
                .show()
        }.addOnSuccessListener { _ ->
            picture_path = "$email/profile.jpg"
        }

    }

    private fun setTextView(list: List<Games>?) {
        list?.forEach {
            if (it.flag) {
                it.textView.setTextColor(resources.getColor(R.color.white, null))
                it.textView.background =
                    resources.getDrawable(R.drawable.textview_rounded_activated, null)
            } else {
                it.textView.setTextColor(resources.getColor(R.color.color_inactivated_blue, null))
                it.textView.background =
                    resources.getDrawable(R.drawable.textview_rounded_inactivated, null)
            }
        }
    }

    private fun setTextView(textView: TextView, flag: Boolean) {
        if (flag) {
            textView.setTextColor(resources.getColor(R.color.white, null))
            textView.background = resources.getDrawable(R.drawable.textview_rounded_activated, null)
        } else {
            textView.setTextColor(resources.getColor(R.color.color_inactivated_blue, null))
            textView.background =
                resources.getDrawable(R.drawable.textview_rounded_inactivated, null)
        }
    }

    private fun getUserPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateGallery()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionContextPopup()
            }
            else -> {
                // TODO 권한 요청
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY
                )
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한 요청")
            .setMessage("앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY
                )
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateGallery()
                } else {
                    Toast.makeText(requireContext(), "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_SUCCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_SUCCESS -> {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {
                    binding.ivUserPicture.setImageURI(selectedImageUri)
                    bitmap = resize(requireContext(), selectedImageUri, 400)
                    binding.btnModifyUserPicture.text = "프로필 사진 변경"
                    pictureFlag = true
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resize(context: Context, uri: Uri, resize: Int): Bitmap {
        var resizeBitmap: Bitmap? = null

        val options = BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)

            var width = options.outWidth
            var height = options.outHeight
            var samplesize = 1;

            while (true) {
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            val temp = BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri),
                null,
                options
            )
            resizeBitmap = temp

        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        }

        return resizeBitmap!!
    }

    private fun modifyUserInfo() {

        GameNameTextViewList.forEach { textView ->
            textView.setOnClickListener {
                GameNameList?.let {
                    changeTextView(it, textView)
                }
            }
        }

        binding.btnSaveUserInfo.setOnClickListener {

            if (pictureFlag) {
                uploadImageToFirebase(bitmap!!)
                uploadImageToCloud(bitmap!!)
                if (picture_path.isNullOrEmpty())
                    picture_path = "$email/profile.jpg"
            }

            if (email.isNotEmpty() && binding.etUserNickName.text.isNotEmpty()) {
                GameNameList?.let { list ->
                    list.forEach {
                        if (it.flag) {
                            GameListToString.add("1")
                            GameListToInt.add(1)
                        } else {
                            GameListToString.add("0")
                            GameListToInt.add(0)
                        }
                    }
                }

                TendencyRadioList.forEach { rb ->
                    if(rb.isChecked) {
                        TendencyListToString.add(rb.text.toString())
                    }
                }

                if (TendencyListToString.size == 4) {
                    saveUserInfoToDB()
                    saveUserInfoToRoom(pictureFlag)

                    Toast.makeText(requireContext(), "정보를 업데이트했습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "${TendencyListToString.size}")
                }

                // TODO 내 프로필 프래그먼트로 전환
                mainActivity.changeFragment()
                mainActivity.removeFragment()
            }
        }
    }

    private fun changeTextView(list: List<Games>?, textView: TextView) {
        list?.forEach {
            if (it.textView.text == textView.text) {
                setTextView(it.textView, !it.flag)
                it.flag = !it.flag
            }
        }
    }

    private fun saveUserInfoToDB() {

        user_name = binding.etUserNickName.toStringFromText()
        game_name = if (binding.etGameUserName.toStringFromText().isEmpty()) null
        else binding.etGameUserName.toStringFromText()
        self_pr = if (binding.etSelfPR.toStringFromText().isEmpty()) null
        else binding.etSelfPR.toStringFromText()

        val tendencyMap = processingTendency(TendencyListToString)
        TendencyListToMap.putAll(ListToMap(TendencyListToString))

        runBlocking {
//            updateData(
//                tendencyMap,
//                GameListToString,
//                email,
//                picture_path,
//                user_name,
//                game_name,
//                self_pr
//            )

            updateFirebase(
                tendencyMap,
                GameListToString,
                FirebaseAuth.getInstance().currentUser!!.uid,
                email,
                picture_path,
                bitPicture,
                user_name,
                game_name,
                self_pr
            )
        }
    }

    private fun updateFirebase(
        paramMap: Map<String, String>,
        paramList: List<String>,
        vararg params: String?
    ) {

        val tendencyMap: Map<String, String> = paramMap
        val gameList: List<String> = paramList
        val uid: String? = params[0]
        val email: String? = params[1]
        val picture: String? = params[2]
        val bitPicture: String? = params[3]
        val user_name: String? = params[4]
        val game_name: String? = params[5]
        val self_pr: String? = params[6]

        val mDatabaseReference = FirebaseDatabase.getInstance().reference.child("$uid")

        val childUpdates = hashMapOf<String, Any>(
            "${FIREBASE_USER}/name" to "$user_name"
        )

        game_name?.let {
            childUpdates["${FIREBASE_USER}/game_name"] = game_name
        }

        picture?.let {
            childUpdates["${FIREBASE_USER}/picture"] = picture
        }

        bitPicture?.let {
            childUpdates["${FIREBASE_USER}/bitPicture"] = bitPicture
        }

        self_pr?.let {
            childUpdates["${FIREBASE_USER}/self_pr"] = self_pr
        }

        tendencyMap.forEach { (t, u) ->
            when (t) {
                "purpose" -> childUpdates["${FIREBASE_TENDENCY}/purpose"] = "$u"
                "voice" -> childUpdates["${FIREBASE_TENDENCY}/voice"] = "$u"
                "men" -> childUpdates["${FIREBASE_TENDENCY}/men"] = "$u"
                "women" -> childUpdates["${FIREBASE_TENDENCY}/women"] = "$u"
                "game_mode" -> childUpdates["${FIREBASE_TENDENCY}/game_mode"] = "$u"
            }
        }

        gameList.forEachIndexed { index, str ->
            childUpdates["${FIREBASE_GAME}/game$index"] = str
        }

        mDatabaseReference.updateChildren(childUpdates)

//        mDatabaseReference.child(FIREBASE_USER).child(uid!!).get().addOnSuccessListener { dataSnapshot ->
//            Log.d(TAG, "user uid : ${dataSnapshot.key}")
//        }.addOnFailureListener {
//            Log.d(TAG, "로그인 실패")
//        }

    }

    private fun updateData(
        paramMap: Map<String, String>,
        paramList: List<String>,
        vararg params: String?
    ) {
        CoroutineScope(Dispatchers.Default).launch {

            val tendencyMap: Map<String, String> = paramMap
            val gameList: List<String> = paramList
            val email: String? = params[0]
            val picture: String? = params[1]
            val user_name: String? = params[2]
            val game_name: String? = params[3]
            val self_pr: String? = params[4]

            // Use Retrofit
            val retrofit = RetrofitClient.getInstance()

            val server = retrofit.create(UserAPI::class.java)
            val call: Call<String> = server.updateUser(
                email!!,
                picture,
                user_name!!,
                game_name!!,
                self_pr,
                tendencyMap["purpose"]!!,
                tendencyMap["voice"]!!,
                tendencyMap["men"]!!,
                tendencyMap["women"]!!,
                tendencyMap["game_mode"]!!,
                gameList[0],
                gameList[1],
                gameList[2],
                gameList[3],
                gameList[4],
                gameList[5],
                gameList[6],
                gameList[7],
                gameList[8],
                gameList[9]
            )
            call.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, "실패 : " + t.localizedMessage)
                    Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<String>,
                    response: retrofit2.Response<String>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "성공 : ${response.body().toString()}")
                    } else {
                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
                    }
                }
            })

        }

    }

    private fun saveUserInfoToRoom(flag: Boolean) {
        runBlocking {
            Thread {
                if (flag) {
                    db.userDAO().updateUserInfo(
                        email,
                        picture_path,
                        null,
                        user_name,
                        game_name,
                        self_pr
                    )
                } else {
                    db.userDAO().updateUserInfo(
                        email,
                        picture_path,
                        currentPhotoPath,
                        user_name,
                        game_name,
                        self_pr
                    )
                }
                db.tendencyDAO().updateUserSimpleInfo(
                    email,
                    TendencyListToMap["purpose"]!!,
                    TendencyListToMap["voice"]!!,
                    TendencyListToMap["preferred_gender"]!!,
                    TendencyListToMap["game_mode"]!!
                )
                db.gameDAO().updateGameInfo(
                    email,
                    GameListToInt[0],
                    GameListToInt[1],
                    GameListToInt[2],
                    GameListToInt[3],
                    GameListToInt[4],
                    GameListToInt[5],
                    GameListToInt[6],
                    GameListToInt[7],
                    GameListToInt[8],
                    GameListToInt[9]
                )

            }.start()
        }
    }

    fun EditText.toStringFromText(): String {
        return this.text.toString()
    }

}