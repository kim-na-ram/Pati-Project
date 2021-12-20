package com.naram.party_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.base.BaseActivity
import com.naram.party_project.databinding.ActivityModifyprofileBinding
import com.naram.party_project.util.Const
import com.naram.party_project.util.Const.Companion.MODIFY_USER_PROFILE
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class ModifyProfileActivity : BaseActivity<ActivityModifyprofileBinding> ({
    ActivityModifyprofileBinding.inflate(it)
}) {

    private val TAG = "ModifyProfile"

    // user detail profile
    private lateinit var TendencyRadioList: List<RadioButton>
    private lateinit var GameNameTextViewList: List<TextView>

    private val TendencyListToString = mutableListOf<String>()
    private val TendencyListToMap = mutableMapOf<String, String>()
    private var GameNameList: List<Games>? = null
    private val GameListToString = mutableListOf<String>()
    private val GameListToInt = mutableListOf<Int>()

    private var bitmap: Bitmap? = null
    private var picturePath: String? = null
    private var pictureFlag: Boolean = false

    private lateinit var email: String
    private lateinit var user_name: String
    private var game_name: String? = null
    private var self_pr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        getRoom()
        modifyUserInfo()
    }

    private fun initViews() {

        setSupportActionBar(binding.layoutTop.toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)

        binding.layoutTop.tvTextToolbar.text = "프로필 수정"
        binding.layoutTop.tvTextToolbar.textSize = 23F

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
                .load(R.drawable.ic_signup_enter_picture_r_128)
                .override(binding.ivUserPicture.width, binding.ivUserPicture.height)
                .into(binding.ivUserPicture)
            pictureFlag = false
            picturePath = null
        }

        binding.btnModifyUserPicture.setOnClickListener {
            getUserPermission()
        }

    }

    private fun getRoom() {
        Thread(Runnable {
            db.userDAO().getUserInfo().forEach {
                runOnUiThread {

                    email = it.email

                    it.picture?.let {
                        picturePath = it
                        uploadImageFromCloud(it)
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
                runOnUiThread {

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
                runOnUiThread {

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

    private fun uploadImageFromCloud(path: String) {
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
                Toast.makeText(this, "사진을 불러오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(
                this,
                "사진 업로드에 실패했습니다.",
                Toast.LENGTH_SHORT
            )
                .show()
        }.addOnSuccessListener { _ ->
            picturePath = "$email/profile.jpg"
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
                this,
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
                    Const.REQUEST_GALLERY
                )
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한 요청")
            .setMessage("앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Const.REQUEST_GALLERY
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
            Const.REQUEST_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateGallery()
                } else {
                    Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, Const.REQUEST_SUCCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            Const.REQUEST_SUCCESS -> {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {
                    binding.ivUserPicture.setImageURI(selectedImageUri)
                    bitmap = resize(this, selectedImageUri, 400)
                    binding.btnModifyUserPicture.text = "프로필 사진 변경"
                    pictureFlag = true
                } else {
                    Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
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
                uploadImageToCloud(bitmap!!)
                if (picturePath.isNullOrEmpty())
                    picturePath = "$email/profile.jpg"
            } else {
                if(binding.ivUserPicture.drawable == null)
                    picturePath = null
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

                    Toast.makeText(this, "정보를 업데이트했습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "${TendencyListToString.size}")
                }

                MODIFY_USER_PROFILE = true

                // 내 프로필 프래그먼트로 전환
                finish()
                startActivity(Intent(this, MainActivity::class.java))

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

        val tendencyMap = Const.processingTendency(TendencyListToString)
        TendencyListToMap.putAll(Const.ListToMap(TendencyListToString))

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
                picturePath,
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
        val user_name: String? = params[3]
        val game_name: String? = params[4]
        val self_pr: String? = params[5]

        val mDatabaseReference = FirebaseDatabase.getInstance().reference.child("$uid")

        val childUpdates = hashMapOf<String, Any>(
            "${Const.FIREBASE_USER}/name" to "$user_name"
        )

        game_name?.let {
            childUpdates["${Const.FIREBASE_USER}/game_name"] = game_name
        }

        picture?.let {
            childUpdates["${Const.FIREBASE_USER}/picture"] = picture
        }

        self_pr?.let {
            childUpdates["${Const.FIREBASE_USER}/self_pr"] = self_pr
        }

        tendencyMap.forEach { (t, u) ->
            when (t) {
                "purpose" -> childUpdates["${Const.FIREBASE_TENDENCY}/purpose"] = "$u"
                "voice" -> childUpdates["${Const.FIREBASE_TENDENCY}/voice"] = "$u"
                "men" -> childUpdates["${Const.FIREBASE_TENDENCY}/men"] = "$u"
                "women" -> childUpdates["${Const.FIREBASE_TENDENCY}/women"] = "$u"
                "game_mode" -> childUpdates["${Const.FIREBASE_TENDENCY}/game_mode"] = "$u"
            }
        }

        gameList.forEachIndexed { index, str ->
            childUpdates["${Const.FIREBASE_GAME}/game$index"] = str
        }

        mDatabaseReference.updateChildren(childUpdates)

    }

//    private fun updateData(
//        paramMap: Map<String, String>,
//        paramList: List<String>,
//        vararg params: String?
//    ) {
//        CoroutineScope(Dispatchers.Default).launch {
//
//            val tendencyMap: Map<String, String> = paramMap
//            val gameList: List<String> = paramList
//            val email: String? = params[0]
//            val picture: String? = params[1]
//            val user_name: String? = params[2]
//            val game_name: String? = params[3]
//            val self_pr: String? = params[4]
//
//            // Use Retrofit
//            val retrofit = RetrofitClient.getInstance()
//
//            val server = retrofit.create(UserAPI::class.java)
//            val call: Call<String> = server.updateUser(
//                email!!,
//                picture,
//                user_name!!,
//                game_name!!,
//                self_pr,
//                tendencyMap["purpose"]!!,
//                tendencyMap["voice"]!!,
//                tendencyMap["men"]!!,
//                tendencyMap["women"]!!,
//                tendencyMap["game_mode"]!!,
//                gameList[0],
//                gameList[1],
//                gameList[2],
//                gameList[3],
//                gameList[4],
//                gameList[5],
//                gameList[6],
//                gameList[7],
//                gameList[8],
//                gameList[9]
//            )
//            call.enqueue(object : Callback<String> {
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d(TAG, "실패 : " + t.localizedMessage)
//                    Toast.makeText(baseContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onResponse(
//                    call: Call<String>,
//                    response: retrofit2.Response<String>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d(TAG, "성공 : ${response.body().toString()}")
//                    } else {
//                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
//                    }
//                }
//            })
//
//        }
//
//    }

    private fun saveUserInfoToRoom(flag: Boolean) {
        runBlocking {
            Thread {
                if (flag) {
                    db.userDAO().updateUserInfo(
                        email,
                        picturePath,
                        null,
                        user_name,
                        game_name,
                        self_pr
                    )
                } else {
                    db.userDAO().updateUserInfo(
                        email,
                        picturePath,
                        null,
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