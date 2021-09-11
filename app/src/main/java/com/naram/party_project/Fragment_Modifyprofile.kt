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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.FileNotFoundException

import com.naram.party_project.PatiConstClass.Companion.IP_ADDRESS
import com.naram.party_project.PatiConstClass.Companion.REQUEST_GALLERY
import com.naram.party_project.PatiConstClass.Companion.REQUEST_SUCCESS
import com.naram.party_project.PatiConstClass.Companion.processingTendency
import com.naram.party_project.databinding.FragmentModifyprofileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayOutputStream

class Fragment_Modifyprofile : Fragment() {

    private val TAG = "Modifyprofile_Fragment"

    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private var _binding: FragmentModifyprofileBinding? = null

    private val binding get() = _binding!!

    // user detail profile
    private lateinit var TendencyTextViewList: List<TextView>
    private lateinit var GameNameTextViewList: List<TextView>

    private var TendencyList: List<Games>? = null
    private val TendencyListToString = mutableListOf<String>()
    private var GameNameList: List<Games>? = null
    private val GameListToString = mutableListOf<String>()
    private val GameListToInt = mutableListOf<Int>()

    private var bitmap: Bitmap? = null
    private var picture_path: String? = null

    private lateinit var email: String
    private lateinit var user_name: String
    private var game_name: String? = null
    private var self_pr: String? = null

    fun newInstance(): Fragment_Modifyprofile {
        return Fragment_Modifyprofile()
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

        binding.btnModifyUserPic.setOnClickListener {
            getUserPermission()
        }

    }

    private fun getRoom() {
        Thread(Runnable {
            db.userDAO().getUserInfo().forEach {
                mainActivity.runOnUiThread {

                    binding.ivUserPicture.setImageDrawable(resources.getDrawable(R.drawable.app_logo))
                    binding.ivUserPicture.setBackgroundColor(resources.getColor(R.color.color_inactivated_blue))
                    binding.ivUserPicture.scaleType = ImageView.ScaleType.CENTER_INSIDE

                    it.picture?.let {
                        uploadImageFromCloud(it)
//                            db.userDAO().updatePicture(it.email, string_uri)
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
                    TendencyList = mutableListOf<Games>().apply {
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
                    GameNameList = mutableListOf<Games>().apply {
                        GameNameTextViewList.forEachIndexed { index, textView ->
                            if (gamenames[index] == 1) this.add(Games(textView, true))
                            else this.add(Games(textView, false))
                            Log.d(
                                TAG,
                                "$index : ${textView.text.toString()} -> ${gamenames[index]}"
                            )
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

        binding.ivUserPicture.scaleType = ImageView.ScaleType.CENTER_CROP

        return result.isSuccessful

    }

    private fun uploadImageFromDownload(Ref: StorageReference) {
        val ONE_MEGABYTE: Long = 1024 * 1024
        Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            val options = BitmapFactory.Options();
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
            binding.ivUserPicture.setImageBitmap(bitmap)
            binding.ivUserPicture.scaleType = ImageView.ScaleType.CENTER_CROP
        }.addOnFailureListener {
            binding.ivUserPicture.setImageDrawable(resources.getDrawable(R.drawable.app_logo))
            binding.ivUserPicture.scaleType = ImageView.ScaleType.CENTER_INSIDE
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
                this@Fragment_Modifyprofile.requireContext(),
                "사진 업로드에 실패했습니다.",
                Toast.LENGTH_SHORT
            )
                .show()
        }.addOnSuccessListener { taskSnapshot ->
//            _userPicture = imagesRef.toString()
            picture_path = "$email/profile.jpg"
            Log.d(TAG, picture_path!!)
        }

    }

    private fun setTextView(list: List<Games>?) {
        list?.forEach {
            if (it.flag) {
                it.textView.setTextColor(resources.getColor(R.color.white))
                it.textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_activated))
            } else {
                it.textView.setTextColor(resources.getColor(R.color.color_inactivated_blue))
                it.textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_inactivated))
            }
        }
    }

    private fun setTextView(textView: TextView, flag: Boolean) {
        if (flag) {
            textView.setTextColor(resources.getColor(R.color.white))
            textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_activated))
        } else {
            textView.setTextColor(resources.getColor(R.color.color_inactivated_blue))
            textView.setBackgroundDrawable(resources.getDrawable(R.drawable.textview_rounded_inactivated))
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
            .setPositiveButton("동의", { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY
                )
            })
            .setNegativeButton("취소", { _, _ -> })
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
            val bitmap = BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri),
                null,
                options
            )
            resizeBitmap = bitmap;

        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        }

        return resizeBitmap!!
    }

    private fun modifyUserInfo() {
        TendencyTextViewList.forEach { textView ->
            textView.setOnClickListener {
                TendencyList?.let {
                    changeTextView(it, textView)
                }
            }
        }

        GameNameTextViewList.forEach { textView ->
            textView.setOnClickListener {
                GameNameList?.let {
                    changeTextView(it, textView)
                }
            }
        }

        binding.btnSaveUserInfo.setOnClickListener {

            if (bitmap != null) {
                uploadImageToCloud(bitmap!!)
                picture_path = "$email/profile.jpg"
            }

            if (email.isNotEmpty() && binding.etUserNickName.text.isNotEmpty()) {
                GameNameList?.let {
                    it.forEach {
                        if (it.flag) {
                            GameListToString.add("1")
                            GameListToInt.add(1)
                        } else {
                            GameListToString.add("0")
                            GameListToInt.add(0)
                        }
                    }
                }

                TendencyList?.let {
                    it.forEach {
                        if (it.flag) {
                            TendencyListToString.add(it.textView.text.toString())
                        }
                    }
                }

                saveUserInfoToDB()
                saveUserInfoToRoom()

                // TODO 내 프로필 프래그먼트로 전환
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

        val TendencyMap: Map<String, String> = processingTendency(TendencyListToString)

        runBlocking {
            updateData(
                TendencyMap,
                GameListToString,
                email,
                picture_path,
                user_name,
                game_name,
                self_pr
            )
        }
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

                override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                    Log.d(TAG, "성공 : " + response?.body().toString())
                }
            })

        }

    }

    private fun saveUserInfoToRoom() {
        runBlocking {
            Thread(Runnable {
                db.userDAO().updateUserInfo(
                    email,
                    picture_path,
                    null,
                    user_name,
                    game_name,
                    self_pr
                )
                db.tendencyDAO().updateUserSimpleInfo(
                    email,
                    TendencyListToString[0],
                    TendencyListToString[1],
                    TendencyListToString[2],
                    TendencyListToString[3],
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

            }).start()
        }
    }

    fun EditText.toStringFromText(): String {
        return this.text.toString()
    }

}