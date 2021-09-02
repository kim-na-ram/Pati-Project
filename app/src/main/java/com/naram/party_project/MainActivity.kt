package com.naram.party_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val toolbar : Toolbar by lazy {
        findViewById(R.id.toolbar)
    }

    private val bnb_menuBar: BottomNavigationView by lazy {
        findViewById(R.id.bnb_menuBar)
    }

    private val vp_showView: ViewPager2 by lazy {
        findViewById(R.id.vp_showView)
    }

    private val Fragment_Myprofile by lazy {
        Fragment_Myprofile()
    }

    private val Fragment_Searchparty by lazy {
        Fragment_Searchparty()
    }

    private val Fragment_Chatting by lazy {
        Fragment_Chatting()
    }

    private val Fragment_Searchscores by lazy {
        Fragment_Searchscores()
    }

    private val Fragment_Setting by lazy {
        Fragment_Setting()
    }

    private val fragments: List<Fragment> = listOf(
        Fragment_Myprofile,
        Fragment_Searchparty,
        Fragment_Chatting,
        Fragment_Searchscores,
        Fragment_Setting
    )

    private val pagerAdapter: MainViewPagerAdapter by lazy {
        MainViewPagerAdapter(this, fragments)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initNavigaionBar()

    }

    private fun initViews() {

        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)

        setViewpager()

    }

    private fun initNavigaionBar() {
        bnb_menuBar.run {
            setOnNavigationItemSelectedListener {
                val page = when (it.itemId) {
                    R.id.myProfile -> 0
                    R.id.searchParty -> 1
                    R.id.chatting -> 2
                    R.id.searchScores -> 3
                    R.id.setting -> 4
                    else -> 0
                }

                if (page != vp_showView.currentItem) {
                    vp_showView.currentItem = page
                }

                true

            }
            selectedItemId = R.id.myProfile
        }
    }

    private fun setViewpager() {

        vp_showView.run {
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val navigation = when (position) {
                        0 -> R.id.myProfile
                        1 -> R.id.searchParty
                        2 -> R.id.chatting
                        3 -> R.id.searchScores
                        4 -> R.id.setting
                        else -> R.id.myProfile
                    }

                    if (bnb_menuBar.selectedItemId != navigation) {
                        bnb_menuBar.selectedItemId = navigation
                    }
                }
            })
        }

    }

}