package com.naram.party_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.naram.party_project.base.BaseActivity
import com.naram.party_project.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding> ({
    ActivityMainBinding.inflate(it)
}) {

    private val myProfileFragment by lazy {
        MyProfileFragment()
    }

    private val searchPartyFragment by lazy {
        SearchPartyFragment()
    }

    private val chattingFragment by lazy {
        ChattingListFragment()
    }

    private val friendsFragment by lazy {
        FriendsFragment()
    }

    private val settingFragment by lazy {
        SettingFragment()
    }

    private val showProfileFragment by lazy {
        ShowProfileFragment()
    }

//    private var fragments: MutableList<Fragment> = mutableListOf(
//        myProfileFragment,
//        searchPartyFragment,
//        chattingFragment,
//        friendsFragment,
//        settingFragment
//    )

//    private val pagerAdapter: MainViewPagerAdapter by lazy {
//        MainViewPagerAdapter(this, fragments)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initNavigationView()

    }

    private fun initViews() {

        setSupportActionBar(binding.toolbar.toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)

//        binding.swipeRefreshLayout.setOnRefreshListener {
//            if (binding.swipeRefreshLayout.isRefreshing) {
//                when(binding.vpShowView.currentItem) {
//                    fragments.indexOf(searchPartyFragment) -> {
//                        flag = false
//                        setupPartyUserList()
//                        searchPartyFragment.refreshLayout()
//                    }
//                    fragments.indexOf(friendsFragment) -> {
//                        // TODO Friends refreshLayout 실행
//                        friendsFragment.refreshLayout()
//                    }
//                    else -> {
//
//                    }
//                }
//                binding.swipeRefreshLayout.isRefreshing = false
//            }
//        }

//        setViewpager()

    }

    private fun initNavigationView() {

        binding.bnbMenuBar.run {
            setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.myProfile -> changeFragment(myProfileFragment)
                    R.id.searchParty -> changeFragment(searchPartyFragment)
                    R.id.chatting -> changeFragment(chattingFragment)
                    R.id.friends -> changeFragment(friendsFragment)
                    R.id.setting -> changeFragment(settingFragment)
                }

//                val page = when (it.itemId) {
//                    R.id.myProfile -> fragments.indexOf(MyProfileFragment())
//                    R.id.searchParty -> fragments.indexOf(searchPartyFragment)
//                    R.id.chatting -> fragments.indexOf(chattingFragment)
//                    R.id.friends -> fragments.indexOf(friendsFragment)
//                    R.id.setting -> fragments.indexOf(settingFragment)
//                    else -> fragments.indexOf(MyProfileFragment())
//                }
//
//                if (page != binding.vpShowView.currentItem) {
//                    binding.vpShowView.currentItem = page
//                }

                true

            }
            selectedItemId = R.id.myProfile
        }
    }

//    private fun setViewpager() {
//
//        binding.vpShowView.run {
//            adapter = pagerAdapter
//            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    val navigation = when (position) {
//                        fragments.indexOf(MyProfileFragment()) -> R.id.myProfile
//                        fragments.indexOf(searchPartyFragment) -> R.id.searchParty
//                        fragments.indexOf(chattingFragment) -> R.id.chatting
//                        fragments.indexOf(friendsFragment) -> R.id.friends
//                        fragments.indexOf(settingFragment) -> R.id.setting
//                        else -> R.id.myProfile
//                    }
//
//                    if (binding.bnbMenuBar.selectedItemId != navigation) {
//                        binding.bnbMenuBar.selectedItemId = navigation
//                    }
//                }
//            })
//        }
//
//    }

//    private fun setupPartyUserList() {
//
//        val email = FirebaseAuth.getInstance().currentUser?.email
//        var gender: String? = null
//
//        runBlocking {
//            val thread = Thread(Runnable {
//                val stringGender = db.tendencyDAO().getTendencyGenderInfo(email!!)
//                gender = when (stringGender) {
//                    "여성 Only" -> "like 'F'"
//                    "남성 Only" -> "like 'M'"
//                    else -> "like 'F' or 'M'"
//                }
//            })
//
//            thread.start()
//            thread.join()
//
//            val retrofit = RetrofitClient.getInstance()
//
//            val server = retrofit.create(UserAPI::class.java)
//
////            server.getAllUser(email!!, gender!!).enqueue(object : Callback<String> {
////                override fun onResponse(
////                    call: Call<String>,
////                    response: Response<String>
////                ) {
////                    if (response.isSuccessful) {
////                        Log.d(TAG, "성공 : ${response.body().toString()}")
////                    } else {
////                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
////                    }
////                }
////
////                override fun onFailure(call: Call<String>, t: Throwable) {
////                }
////            })
//
//            server.getAllUser(email!!, gender!!).enqueue(object : Callback<List<Party>> {
//                override fun onResponse(
//                    call: Call<List<Party>>,
//                    response: Response<List<Party>>
//                ) {
//                    if (response.isSuccessful) {
//                        list.clear()
//                        Log.d(TAG, "list is changed")
//                        response.body()!!.forEachIndexed { index, item ->
//                            list.add(item)
//                            item.picture?.let { path ->
//                                val MAX_BYTE: Long = 400 * 400
//                                val imagesRef = Firebase.storage.reference.child(path)
//
//                                imagesRef.getBytes(MAX_BYTE).addOnSuccessListener {
//                                    val options = BitmapFactory.Options();
//                                    val bitmap =
//                                        BitmapFactory.decodeByteArray(it, 0, it.size, options)
//                                    list[index].bitmap = bitmap
//                                }.addOnFailureListener {
//                                    Toast.makeText(
//                                        binding.root.context,
//                                        "사진을 불러오는데 오류가 발생했습니다.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        }
//                    } else {
//                        Log.d(TAG, "실패 : ${response.errorBody().toString()}")
//                        list.clear()
//                    }
//                }
//
//                override fun onFailure(call: Call<List<Party>>, t: Throwable) {
//                    Log.d(TAG, "실패 : ${t.localizedMessage}")
//                    list.clear()
//                }
//            })
//        }
//
//    }
//
//    fun getPartyUserProfile(email: String) {
//        val retrofit = RetrofitClient.getInstance()
//
//        val server = retrofit.create(UserAPI::class.java)
//
//        server.getUser(email).enqueue(object : Callback<User> {
//            override fun onResponse(
//                call: Call<User>,
//                response: Response<User>
//            ) {
//                if (response.isSuccessful) {
//                    user = response.body()!!
//                    appearFragment()
//                } else {
//                    Log.d(TAG, "실패 : ${response.errorBody().toString()}")
//                }
//            }
//
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                Log.d(TAG, "실패 : ${t.localizedMessage}")
//            }
//        })
//    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.flMainContainer, fragment)
            .commit()

//        fragments.add(modifyProfileFragment)

//        val id = binding.vpShowView.currentItem
//        binding.vpShowView.currentItem = when (id) {
//            fragments.indexOf(myProfileFragment) -> fragments.indexOf(modifyProfileFragment)
//            fragments.indexOf(modifyProfileFragment) -> {
//                fragments.indexOf(MyProfileFragment())
//            }
//            else -> fragments.indexOf(MyProfileFragment())
//        }

    }

//    override fun onBackPressed() {
//        when(searchPartyFragment.binding.flShowPartyProfile.isVisible) {
//            true -> searchPartyFragment.disappearFragment()
//            else -> super.onBackPressed()
//        }
//    }

    fun getDB() : AppDatabase {
        return db
    }

}