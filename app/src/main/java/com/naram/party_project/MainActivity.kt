package com.naram.party_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.naram.party_project.callback.Party
import com.naram.party_project.callback.Profile
import com.naram.party_project.databinding.ActivityMainBinding
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = "Main"

    private lateinit var binding: ActivityMainBinding

    private lateinit var db: AppDatabase

    private var list = mutableListOf<Party>()
    private var profile : Profile? = null

    private val Fragment_Myprofile by lazy {
        Fragment_Myprofile()
    }

    private val Fragment_Searchparty by lazy {
        Fragment_Searchparty()
    }

    private val Fragment_Chatting by lazy {
        Fragment_Chatting()
    }

    private val Fragment_Friends by lazy {
        Fragment_Friends()
    }

    private val Fragment_Setting by lazy {
        Fragment_Setting()
    }

    private val Fragment_Modifyprofile by lazy {
        Fragment_Modifyprofile()
    }

    private val Fragment_Showprofile by lazy {
        Fragment_Showprofile()
    }

    private var fragments: MutableList<Fragment> = mutableListOf(
        Fragment_Myprofile,
        Fragment_Searchparty,
        Fragment_Chatting,
        Fragment_Friends,
        Fragment_Setting
    )

    private val pagerAdapter: MainViewPagerAdapter by lazy {
        MainViewPagerAdapter(this, fragments)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        createDB()
        initViews()
        initNavigationBar()
        setupPartyUserList()

    }

    private fun createDB() {
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "userDB"
        )
            .build()
    }

    private fun initViews() {

        setSupportActionBar(binding.toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)

        setViewpager()

    }

    private fun initNavigationBar() {

        binding.bnbMenuBar.run {
            setOnItemSelectedListener {
                val page = when (it.itemId) {
                    R.id.myProfile -> fragments.indexOf(Fragment_Myprofile)
                    R.id.searchParty -> fragments.indexOf(Fragment_Searchparty)
                    R.id.chatting -> fragments.indexOf(Fragment_Chatting)
                    R.id.friends -> fragments.indexOf(Fragment_Friends)
                    R.id.setting -> fragments.indexOf(Fragment_Setting)
                    else -> fragments.indexOf(Fragment_Myprofile)
                }

                if (page != binding.vpShowView.currentItem) {
                    binding.vpShowView.currentItem = page
                }

                true

            }
            selectedItemId = R.id.myProfile
        }
    }

    private fun setViewpager() {

        binding.vpShowView.run {
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val navigation = when (position) {
                        fragments.indexOf(Fragment_Myprofile) -> R.id.myProfile
                        fragments.indexOf(Fragment_Searchparty) -> R.id.searchParty
                        fragments.indexOf(Fragment_Chatting) -> R.id.chatting
                        fragments.indexOf(Fragment_Friends) -> R.id.friends
                        fragments.indexOf(Fragment_Setting) -> R.id.setting
                        else -> R.id.myProfile
                    }

                    if (binding.bnbMenuBar.selectedItemId != navigation) {
                        binding.bnbMenuBar.selectedItemId = navigation
                    }
                }
            })
        }

    }

    private fun setupPartyUserList() {

        val email = FirebaseAuth.getInstance().currentUser?.email
        var gender: String? = null

        runBlocking {
            val thread = Thread(Runnable {
                val stringGender = db.tendencyDAO().getTendencyGenderInfo(email!!)
                gender = when (stringGender) {
                    "여성 Only" -> "like 'F'"
                    "남성 Only" -> "like 'M'"
                    else -> "like 'F' or 'M'"
                }
            })

            thread.start()
            thread.join()

            val retrofit = RetrofitClient.getInstance()

            val server = retrofit.create(UserAPI::class.java)

//            server.getAllUser(email!!, gender!!).enqueue(object : Callback<String> {
//                override fun onResponse(
//                    call: Call<String>,
//                    response: Response<String>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d(TAG, "성공 : ${response.body().toString()}")
//                    } else {
//                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                }
//            })

            server.getAllUser(email!!, gender!!).enqueue(object : Callback<List<Party>> {
                override fun onResponse(
                    call: Call<List<Party>>,
                    response: Response<List<Party>>
                ) {
                    if (response.isSuccessful) {
                        list.clear()
                        list.addAll(response.body()!!)
                    } else {
                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
                        list.clear()
                    }
                }

                override fun onFailure(call: Call<List<Party>>, t: Throwable) {
                    Log.d(TAG, "실패 : ${t.localizedMessage}")
                    list.clear()
                }
            })
        }

    }

    fun getPartyUserProfile(email: String) {
        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

        server.getUser(email).enqueue(object : Callback<Profile> {
            override fun onResponse(
                call: Call<Profile>,
                response: Response<Profile>
            ) {
                if (response.isSuccessful) {
                    profile = response.body()!!
                    appearFragment()
                } else {
                    Log.d(TAG, "실패 : ${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.d(TAG, "실패 : ${t.localizedMessage}")
            }
        })
    }

    fun getProfile() : Profile? {
        return profile
    }

    fun changeFragment() {
        fragments.add(Fragment_Modifyprofile)

        val id = binding.vpShowView.currentItem
        binding.vpShowView.currentItem = when (id) {
            fragments.indexOf(Fragment_Myprofile) -> fragments.indexOf(Fragment_Modifyprofile)
            fragments.indexOf(Fragment_Modifyprofile) -> {
                setupPartyUserList()
                fragments.indexOf(Fragment_Myprofile)
            }
            else -> fragments.indexOf(Fragment_Myprofile)
        }

    }

    fun removeFragment() {
        fragments.remove(Fragment_Modifyprofile)
    }

    fun appearFragment() {
        binding.flShowPartyProfile.visibility = View.VISIBLE

        val fragment = Fragment_Showprofile()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fl_showPartyProfile, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun disappearFragment() {
        supportFragmentManager.popBackStack()
        binding.flShowPartyProfile.visibility = View.GONE
    }

    override fun onBackPressed() {
        if(binding.flShowPartyProfile.isVisible) {
            disappearFragment()
        } else when (binding.vpShowView.currentItem) {
            fragments.indexOf(Fragment_Modifyprofile) -> {
                binding.vpShowView.currentItem = fragments.indexOf(Fragment_Myprofile)
                fragments.remove(Fragment_Modifyprofile)
            }
//            fragments.indexOf(Fragment_Searchparty) -> {
//                val fragmentList = supportFragmentManager.fragments
//                fragmentList.forEach {
//                    if (it.id == Fragment_Showprofile.id) {
//                        if (it.isVisible) {
//                            disappearFragment()
//                            return@forEach
//                        }
//                        else if(!it.isVisible) {
//                            super.onBackPressed()
//                            return@forEach
//                        }
//                    }
//                }
//            }
            else -> super.onBackPressed()
        }
    }

    fun getList(): List<Party> {
        return list
    }

}