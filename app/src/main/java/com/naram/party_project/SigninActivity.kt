package com.naram.party_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

import com.naram.party_project.databinding.ActivitySigninBinding
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_GAME_MODE
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_PREFERRED_GENDER_MEN
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_PREFERRED_GENDER_WOMEN
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_PURPOSE
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_VOICE
import com.naram.party_project.PatiConstClass.Companion.processingTendency
import com.naram.party_project.callback.Profile
import com.naram.party_project.model.Game
import com.naram.party_project.model.Tendency
import com.naram.party_project.model.User
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {

    private val TAG = "SigninActivity"

    private lateinit var db: AppDatabase

    private var userProfile: UserProfile? = null

    private var firebaseAuth: FirebaseAuth? = null

    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        createDB()
        initViews()

    }

    private fun createDB() {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "userDB"
        )
            .build()
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
            // TODO firebase 비밀번호 찾기
        }

        binding.tvUserSignup.setOnClickListener {
            finish()
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

        val email = FirebaseAuth.getInstance().currentUser?.email

        runBlocking {
            getData(email!!)
        }

    }

    private suspend fun getData(vararg params: String) {
        val deferred = CoroutineScope(Dispatchers.IO).async {
            val email: String = params[0]

            val retrofit = RetrofitClient.getInstance()

            val server = retrofit.create(UserAPI::class.java)

            server.getUser(email!!).enqueue(object : Callback<Profile> {
                override fun onResponse(
                    call: Call<Profile>,
                    response: Response<Profile>
                ) {
                    if(response.isSuccessful) {
                        Log.d(TAG, "성공 : ${response.body().toString()}")
                        showResult(response.body())
                    } else {
                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
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

    private fun showResult(response: Profile?) {
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

        IntGameList.forEach {
            if(it == 1)
                games.add(true)
            else games.add(false)
        }

        userProfile = UserProfile(
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
            userProfile?.let {
                db.userDAO().insertUserInfo(
                    User(
                        userProfile!!._userEmail,
                        userProfile!!._userPicture,
                        null,
                        userProfile!!._userNickName,
                        userProfile!!._userGameName,
                        userProfile!!._userGender,
                        userProfile!!._userPR
                    )
                )

                db.tendencyDAO().insertTendencyInfo(
                    Tendency(
                        userProfile!!._userEmail,
                        userProfile!!._userGameTendency[0],
                        userProfile!!._userGameTendency[1],
                        userProfile!!._userGameTendency[2],
                        userProfile!!._userGameTendency[3]
                    )
                )

                db.gameDAO().insertGameInfo(
                    Game(
                        userProfile!!._userEmail,
                        userProfile!!._userGameNamesInt!!.get(0),
                        userProfile!!._userGameNamesInt!!.get(1),
                        userProfile!!._userGameNamesInt!!.get(2),
                        userProfile!!._userGameNamesInt!!.get(3),
                        userProfile!!._userGameNamesInt!!.get(4),
                        userProfile!!._userGameNamesInt!!.get(5),
                        userProfile!!._userGameNamesInt!!.get(6),
                        userProfile!!._userGameNamesInt!!.get(7),
                        userProfile!!._userGameNamesInt!!.get(8),
                        userProfile!!._userGameNamesInt!!.get(9)
                    )
                )

            }

        })

        thread.start()

        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)

    }

}