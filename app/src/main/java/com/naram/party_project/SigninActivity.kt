package com.naram.party_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

import com.naram.party_project.PatiConstClass.Companion.IP_ADDRESS
import com.naram.party_project.PatiConstClass.Companion.TAG_EMAIL
import com.naram.party_project.PatiConstClass.Companion.TAG_GAME
import com.naram.party_project.PatiConstClass.Companion.TAG_GAME_NAME
import com.naram.party_project.PatiConstClass.Companion.TAG_GENDER
import com.naram.party_project.PatiConstClass.Companion.TAG_PICTURE
import com.naram.party_project.PatiConstClass.Companion.TAG_RESULTS
import com.naram.party_project.PatiConstClass.Companion.TAG_SELFPR
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_GAME_MODE
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_PREFERRED_GENDER
import com.naram.party_project.PatiConstClass.Companion.TAG_USER_NAME
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_PURPOSE
import com.naram.party_project.PatiConstClass.Companion.TAG_TENDENCY_VOICE
import com.naram.party_project.PatiConstClass.Companion.processingTendency
import com.naram.party_project.model.User
import kotlinx.coroutines.*

class SigninActivity : AppCompatActivity() {

    private val SELECT_URL = "http://$IP_ADDRESS/userSelect.php"
    private val TAG = "SigninActivity"

    private lateinit var db: AppDatabase

    private var userProfile: UserProfile? = null

    private var firebaseAuth: FirebaseAuth? = null

    private val et_signinUserEmail: EditText by lazy {
        findViewById(R.id.et_signinUserEmail)
    }

    private val et_signinUserPW: EditText by lazy {
        findViewById(R.id.et_signinUserPW)
    }

    private val btn_userSignin: Button by lazy {
        findViewById(R.id.btn_userSignin)
    }

    private val tv_userSignup: TextView by lazy {
        findViewById(R.id.tv_userSignup)
    }

    private val tv_findAccount: TextView by lazy {
        findViewById(R.id.tv_findAccount)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

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

        btn_userSignin.setOnClickListener {
            val userEmail = et_signinUserEmail.text.toString()
            val userPW = et_signinUserPW.text.toString()

            if (userEmail.isEmpty() || userPW.isEmpty()) return@setOnClickListener
            else {
                checkFirebaseAuth(userEmail, userPW)

                val result: String

                runBlocking {
                    result = getData(SELECT_URL, userEmail)
                    showResult(result)
                }

                Thread(Runnable {
                    db.userDAO().deleteAll()

                    db.userDAO().insertUserInfo(
                        User(
                            userProfile!!._userEmail,
                            userProfile!!._userPicture,
                            null,
                            userProfile!!._userNickName,
                            userProfile!!._userGameName,
                            userProfile!!._userGender,
                            userProfile!!._userPR,
                            userProfile!!._userGameTendency[0],
                            userProfile!!._userGameTendency[1],
                            userProfile!!._userGameTendency[2],
                            userProfile!!._userGameTendency[3],
                            userProfile!!._userGameNames[0],
                            userProfile!!._userGameNames[1],
                            userProfile!!._userGameNames[2],
                            userProfile!!._userGameNames[3],
                            userProfile!!._userGameNames[4],
                            userProfile!!._userGameNames[5],
                            userProfile!!._userGameNames[6],
                            userProfile!!._userGameNames[7],
                            userProfile!!._userGameNames[8],
                            userProfile!!._userGameNames[9]
                        )
                    )
                }).start()

                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            }

        }

        tv_findAccount.setOnClickListener {
            // TODO firebase 비밀번호 찾기
        }

        tv_userSignup.setOnClickListener {
            finish()
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }

    private fun checkFirebaseAuth(userID: String, userPW: String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth!!.signInWithEmailAndPassword(userID, userPW).addOnCompleteListener {
            if (it.isSuccessful) {
                return@addOnCompleteListener
            } else {
                Toast.makeText(this, "이메일 혹은 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun getData(vararg params: String?): String {
        val deferred = CoroutineScope(Dispatchers.IO).async {
            val serverURL: String? = params[0]
            val email: String? = params[1]

            val postParameters = "email=$email"

            val url = URL(serverURL)
            val httpURLConnection: HttpURLConnection =
                url.openConnection() as HttpURLConnection
            httpURLConnection.setReadTimeout(5000)
            httpURLConnection.setConnectTimeout(5000)
            httpURLConnection.setRequestMethod("POST")
            httpURLConnection.setDoInput(true)
            httpURLConnection.connect()
            val outputStream: OutputStream = httpURLConnection.getOutputStream()
            outputStream.write(postParameters.toByteArray(charset("UTF-8")))
            outputStream.flush()
            outputStream.close()
            val responseStatusCode: Int = httpURLConnection.getResponseCode()
            Log.d(TAG, "response code - $responseStatusCode")
            val inputStream: InputStream
            inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                httpURLConnection.getInputStream()
            } else {
                httpURLConnection.getErrorStream()
            }
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            bufferedReader.close()

            Log.d(TAG, sb.toString().trim { it <= ' ' })

            sb.toString().trim { it <= ' ' }
        }

        val result: String = try {
            deferred.await()
        } catch (e: Exception) {
            Log.d(TAG, "Error" + e)

            e.toString()
        }

        return result

    }

    private fun showResult(result: String) {
        try {
            val jsonObject = JSONObject(result)
            val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_RESULTS)

            val user = jsonArray.getJSONObject(0)

            // Simple User Info
            val db_email = user.getString(TAG_EMAIL)

            val db_username = user.getString(TAG_USER_NAME)

            var db_gamename: String? = null
            if (!user.isNull(TAG_GAME_NAME))
                db_gamename = user.getString(TAG_GAME_NAME)

            var db_userpicture: String? = null
            if (!user.isNull(TAG_PICTURE))
                db_userpicture = user.getString(TAG_PICTURE)

            val db_usergender = user.getString(TAG_GENDER)

            val db_userpr = user.getString(TAG_SELFPR)

            // Game Tendency
            val TendencyMap = mutableMapOf<String, String>().apply {
                this[TAG_TENDENCY_PURPOSE] = user.getString(TAG_TENDENCY_PURPOSE)
                this[TAG_TENDENCY_VOICE] = user.getString(TAG_TENDENCY_VOICE)
                this[TAG_TENDENCY_PREFERRED_GENDER] =
                    user.getString(TAG_TENDENCY_PREFERRED_GENDER)
                this[TAG_TENDENCY_GAME_MODE] = user.getString(TAG_TENDENCY_GAME_MODE)
            }

            val tendency = processingTendency(TendencyMap)

            val gamenames = mutableListOf<Boolean>()

            for (j in 0..9) {
                val k = user.getInt(TAG_GAME + j)

                if (k == 1)
                    gamenames.add(true)
                else
                    gamenames.add(false)
            }

            userProfile = UserProfile(
                db_email,
                db_userpicture,
                db_username,
                db_gamename,
                db_usergender,
                db_userpr,
                tendency,
                gamenames
            )

        } catch (e: JSONException) {
            Log.d(TAG, e.toString())
        }

    }

}