package com.naram.party_project.ui.signin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.naram.party_project.ui.account.FindAccountActivity
import com.naram.party_project.R
import com.naram.party_project.ui.signup.SignupActivity
import com.naram.party_project.ui.base.BaseActivity
import com.naram.party_project.data.remote.model.Profile
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import java.lang.Exception

import com.naram.party_project.databinding.ActivitySigninBinding
import com.naram.party_project.util.Const.Companion.TAG_TENDENCY_GAME_MODE
import com.naram.party_project.util.Const.Companion.TAG_TENDENCY_PREFERRED_GENDER_MEN
import com.naram.party_project.util.Const.Companion.TAG_TENDENCY_PREFERRED_GENDER_WOMEN
import com.naram.party_project.util.Const.Companion.TAG_TENDENCY_PURPOSE
import com.naram.party_project.util.Const.Companion.TAG_TENDENCY_VOICE
import com.naram.party_project.util.Const.Companion.processingTendency
import com.naram.party_project.data.local.Game
import com.naram.party_project.data.local.Tendency
import com.naram.party_project.data.local.User
import com.naram.party_project.data.remote.api.RetrofitClient
import com.naram.party_project.data.remote.api.UserAPI
import com.naram.party_project.ui.main.MainActivity
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
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_GAME_MODE
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_PREFERRED_GENDER_MEN
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_PURPOSE
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_VOICE
import com.naram.party_project.util.Const.Companion.FIREBASE_USERS
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_EMAIL
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GAME_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GENDER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_SELF_PR
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : BaseActivity<ActivitySigninBinding>({
    ActivitySigninBinding.inflate(it)
}) {

    private val TAG = "Sign In"

    private var profile: Profile? = null

    private var firebaseAuth: FirebaseAuth? = null

    private var tmpEmail : String? = null
    private var tmpName : String? = null
    private var tmpGameName : String? = null
    private var tmpGender : String? = null
    private var tmpPicture : String? = null
    private var tmpSelfPR : String? = null
    private var tmpPurpose : String? = null
    private var tmpVoice : String? = null
    private var tmpMen : String? = null
    private var tmpWomen : String? = null
    private var tmpGameMode : String? = null
    private var tmpGame0 : String? = null
    private var tmpGame1 : String? = null
    private var tmpGame2 : String? = null
    private var tmpGame3 : String? = null
    private var tmpGame4 : String? = null
    private var tmpGame5 : String? = null
    private var tmpGame6 : String? = null
    private var tmpGame7 : String? = null
    private var tmpGame8 : String? = null
    private var tmpGame9 : String? = null

    var tendency = listOf<String>()
    var gameIntList = listOf<Int>()
    val games = mutableListOf<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

    }

    private fun initViews() {

        binding.btnUserSignin.setOnClickListener {
            val userEmail = binding.etSigninUserEmail.text.toString()
            val userPW = binding.etSigninUserPW.text.toString()

            if (userEmail.isEmpty() || userPW.isEmpty()) return@setOnClickListener
            else {
                checkFirebaseAuth(userEmail, userPW)
            }

        }

        binding.tvFindAccount.setOnClickListener {
            val intent = Intent(this, FindAccountActivity::class.java)
            startActivity(intent)
        }

        binding.tvUserSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }

    private fun checkFirebaseAuth(userID: String, userPW: String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth!!.signInWithEmailAndPassword(userID, userPW).addOnCompleteListener {
            if(it.isSuccessful) {
                userInfo()
            } else {
                Toast.makeText(this, "이메일 혹은 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }
        }
    }

    private fun userInfo() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email

        showProgressbar(true)
        getFirebaseData(uid!!)

        Handler(Looper.getMainLooper()).postDelayed({
            showResult()
            showProgressbar(false)
        }, 3000)

//        runBlocking {
//            getFirebaseData(uid!!)
//            getData(email!!)
//            showResult()
//        }

    }

    private fun getFirebaseData(uid : String) {

        val mDatabaseReference = FirebaseDatabase.getInstance().reference.child(FIREBASE_USERS).child(uid)

        mDatabaseReference.child(FIREBASE_USER).get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach {
                when(it.key) {
                    FIREBASE_USER_EMAIL -> {
                        tmpEmail = it.value.toString()
                        Log.d(TAG, "로그인 성공 : $tmpEmail")
                    }
                    FIREBASE_USER_NAME -> tmpName = it.value.toString()
                    FIREBASE_USER_GENDER -> tmpGender = it.value.toString()
                    FIREBASE_USER_PICTURE -> tmpPicture = it.value.toString()
                    FIREBASE_USER_GAME_NAME -> tmpGameName = it.value.toString()
                    FIREBASE_USER_SELF_PR -> tmpSelfPR = it.value.toString()
                }
            }
        }.addOnFailureListener {
            Log.d(TAG, "로그인 실패 : ${it.printStackTrace()}")
        }

        mDatabaseReference.child(FIREBASE_TENDENCY).get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach {
                when(it.key) {
                    FIREBASE_TENDENCY_PURPOSE -> {
                        tmpPurpose = it.value.toString()
                        Log.d(TAG, "tendency is $tmpPurpose")
                    }
                    FIREBASE_TENDENCY_VOICE -> tmpVoice = it.value.toString()
                    FIREBASE_TENDENCY_PREFERRED_GENDER_MEN -> tmpMen = it.value.toString()
                    FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN -> tmpWomen = it.value.toString()
                    FIREBASE_TENDENCY_GAME_MODE -> tmpGameMode = it.value.toString()
                }
            }
        }

        mDatabaseReference.child(FIREBASE_GAME).get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach {
                when(it.key) {
                    FIREBASE_GAME_0_LOL -> tmpGame0 = it.value.toString()
                    FIREBASE_GAME_1_OVER_WATCH -> tmpGame1 = it.value.toString()
                    FIREBASE_GAME_2_BATTLE_GROUND -> tmpGame2 = it.value.toString()
                    FIREBASE_GAME_3_SUDDEN_ATTACK -> tmpGame3 = it.value.toString()
                    FIREBASE_GAME_4_FIFA_ONLINE_4 -> tmpGame4 = it.value.toString()
                    FIREBASE_GAME_5_LOST_ARK -> tmpGame5 = it.value.toString()
                    FIREBASE_GAME_6_MAPLE_STORY -> tmpGame6 = it.value.toString()
                    FIREBASE_GAME_7_CYPHERS -> tmpGame7 = it.value.toString()
                    FIREBASE_GAME_8_STAR_CRAFT -> tmpGame8 = it.value.toString()
                    FIREBASE_GAME_9_DUNGEON_AND_FIGHTER -> {
                        tmpGame9 = it.value.toString()
                        Log.d(TAG, "game9 is $tmpGame9")
                    }
                }
            }
        }

    }

    private fun showResult() {
        val tendencyMap = mutableMapOf<String, String>().apply {
            this[TAG_TENDENCY_PURPOSE] = tmpPurpose!!
            this[TAG_TENDENCY_VOICE] = tmpVoice!!
            this[TAG_TENDENCY_PREFERRED_GENDER_WOMEN] = tmpWomen!!
            this[TAG_TENDENCY_PREFERRED_GENDER_MEN] = tmpMen!!
            this[TAG_TENDENCY_GAME_MODE] = tmpGameMode!!
        }

        val tendency = processingTendency(tendencyMap)

        val intGameList = listOf(
            tmpGame0!!.toInt(),
            tmpGame1!!.toInt(),
            tmpGame2!!.toInt(),
            tmpGame3!!.toInt(),
            tmpGame4!!.toInt(),
            tmpGame5!!.toInt(),
            tmpGame6!!.toInt(),
            tmpGame7!!.toInt(),
            tmpGame8!!.toInt(),
            tmpGame9!!.toInt()
        )

        val games = mutableListOf<Boolean>()

        intGameList.forEach {
            if(it == 1)
                games.add(true)
            else games.add(false)
        }

        profile = Profile(
            tmpEmail!!,
            tmpPicture,
            tmpName!!,
            if(tmpGameName.isNullOrEmpty()) null else tmpGameName,
            tmpGender!!,
            if(tmpSelfPR.isNullOrEmpty()) null else tmpSelfPR,
            tendency,
            games,
            intGameList
        )

        saveInfoToRoom()

    }

    private fun showProgressbar(flag : Boolean) {

        if(flag) {
            binding.pbShowLoginProgress.visibility = View.VISIBLE
            val rotateAnimation =
                AnimationUtils.loadAnimation(baseContext, R.anim.animation_progressbar)
            binding.pbShowLoginProgress.startAnimation(rotateAnimation)
        } else {
            binding.pbShowLoginProgress.clearAnimation()
            binding.pbShowLoginProgress.visibility = View.GONE
        }

    }

    private suspend fun getData(vararg params: String) {
        val deferred = CoroutineScope(Dispatchers.IO).async {
            val email: String = params[0]

            val retrofit = RetrofitClient.getInstance()

            val server = retrofit.create(UserAPI::class.java)

            server.getUser(email!!).enqueue(object : Callback<com.naram.party_project.data.remote.model.User> {
                override fun onResponse(
                    call: Call<com.naram.party_project.data.remote.model.User>,
                    response: Response<com.naram.party_project.data.remote.model.User>
                ) {
                    if(response.isSuccessful) {
                        Log.d(TAG, "성공 : ${response.body().toString()}")
                        showResult(response.body())
                    } else {
                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
                    }
                }

                override fun onFailure(call: Call<com.naram.party_project.data.remote.model.User>, t: Throwable) {
                    Log.d(TAG, "실패 : ${t.localizedMessage}")
                    FirebaseAuth.getInstance()?.signOut()
                }
            })

        }

        try {
            deferred.await()
        } catch (e: Exception) {
            Log.d(TAG, "Error$e")
            e.toString()
        }

    }

    private fun showResult(response: com.naram.party_project.data.remote.model.User?) {
        // Game Tendency
        val TendencyMap = mutableMapOf<String, String>().apply {
            this[TAG_TENDENCY_PURPOSE] = response!!.purpose
            this[TAG_TENDENCY_VOICE] = response!!.voice
            this[TAG_TENDENCY_PREFERRED_GENDER_WOMEN] = response!!.women
            this[TAG_TENDENCY_PREFERRED_GENDER_MEN] = response!!.men
            this[TAG_TENDENCY_GAME_MODE] = response!!.game_mode
        }

        val tendency = processingTendency(TendencyMap)

        val IntGameList = listOf(
            response!!.game0.toInt(),
            response!!.game1.toInt(),
            response!!.game2.toInt(),
            response!!.game3.toInt(),
            response!!.game4.toInt(),
            response!!.game5.toInt(),
            response!!.game6.toInt(),
            response!!.game7.toInt(),
            response!!.game8.toInt(),
            response!!.game9.toInt()
        )

        val games = mutableListOf<Boolean>()

        profile = Profile(
            response!!.email,
            response!!.picture,
            response!!.user_name,
            response!!.game_name,
            response!!.gender,
            response!!.self_pr,
            tendency,
            games,
            IntGameList
        )

        saveInfoToRoom()

    }

    private fun saveInfoToRoom() {
        val thread = Thread(Runnable {
            profile?.let {
                db.userDAO().insertUserInfo(
                    User(
                        profile!!.email,
                        profile!!.picture,
                        profile!!.nickName,
                        profile!!.gameName,
                        profile!!.gender,
                        profile!!.selfPR
                    )
                )

                db.tendencyDAO().insertTendencyInfo(
                    Tendency(
                        profile!!.email,
                        profile!!.gameTendency[0],
                        profile!!.gameTendency[1],
                        profile!!.gameTendency[2],
                        profile!!.gameTendency[3]
                    )
                )

                db.gameDAO().insertGameInfo(
                    Game(
                        profile!!.email,
                        profile!!.gameNamesInt!![0],
                        profile!!.gameNamesInt!![1],
                        profile!!.gameNamesInt!![2],
                        profile!!.gameNamesInt!![3],
                        profile!!.gameNamesInt!![4],
                        profile!!.gameNamesInt!![5],
                        profile!!.gameNamesInt!![6],
                        profile!!.gameNamesInt!![7],
                        profile!!.gameNamesInt!![8],
                        profile!!.gameNamesInt!![9]
                    )
                )

            }

        })

        thread.start()
        thread.join()

        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)

    }

}