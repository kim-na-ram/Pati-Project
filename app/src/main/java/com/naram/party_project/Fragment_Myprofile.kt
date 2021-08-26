package com.naram.party_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.os.Bundle as Bundle

class Fragment_Myprofile : Fragment() {

    class Games(_textView : TextView, _flag : Boolean) {
        val textView : TextView
        val flag : Boolean

        init {
            textView = _textView
            flag = _flag
        }

    }

    // user simple profile
    private lateinit var iv_userPicture : ImageView
    private lateinit var tv_userNickName : TextView
    private lateinit var tv_gameUserName : TextView
    private lateinit var tv_realKindness : TextView
    private lateinit var tv_estimatedKindness : TextView
    private var NumberofAppraiser : Int = 0

    // user detail profile
    private lateinit var tv_gameTendency_wantWin : TextView
    private lateinit var tv_gameTendency_winOrlose : TextView
    private lateinit var tv_gameTendency_onlyWin : TextView
    private lateinit var tv_gameTendency_onlyFun : TextView
    private lateinit var tv_gameTendency_voiceOk : TextView
    private lateinit var tv_gameTendency_voiceNo : TextView
    private lateinit var tv_gameTendency_onlyWomen : TextView
    private lateinit var tv_gameTendency_onlyMen : TextView
    private lateinit var tv_gameTendency_womenOrmen : TextView

    private lateinit var tv_gameNames_LOL : TextView
    private lateinit var tv_gameNames_OverWatch : TextView
    private lateinit var tv_gameNames_BattleGround : TextView
    private lateinit var tv_gameNames_SuddenAttack : TextView
    private lateinit var tv_gameNames_FIFAOnline4 : TextView
    private lateinit var tv_gameNames_LostArk : TextView
    private lateinit var tv_gameNames_MapleStory : TextView
    private lateinit var tv_gameNames_Cyphers : TextView
    private lateinit var tv_gameNames_StarCraft : TextView
    private lateinit var tv_gameNames_DungeonandFighter : TextView
//    private lateinit var tv_gameNames_DeadbyDaylight : TextView
//    private lateinit var tv_gameNames_StardewValley : TextView

    // modify button
    private lateinit var btn_modifyUserInfo : Button

    private val Fragment_ModifyUserInfo by lazy {
        Fragment_ModifyUserInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_myprofile, container, false)

        initViews(view)
        getUserProfile()

        btn_modifyUserInfo.setOnClickListener {
            // TODO 버튼 클릭 시 정보 수정 화면으로 이동
            childFragmentManager.beginTransaction()
                .replace(R.id.fl_modifyUserInfo, Fragment_ModifyUserInfo)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }

        return view

    }

    private fun initViews(view: View) {

        // user simple profile
        iv_userPicture = view.findViewById(R.id.iv_userPicture)
        tv_userNickName = view.findViewById(R.id.tv_userNickName)
        tv_gameUserName = view.findViewById(R.id.tv_gameUserName)
        tv_realKindness = view.findViewById(R.id.tv_realKindness)
        tv_estimatedKindness = view.findViewById(R.id.tv_estimatedKindness)

        // user detail profile
        tv_gameTendency_wantWin = view.findViewById(R.id.tv_gameTendency_wantWin)
        tv_gameTendency_winOrlose = view.findViewById(R.id.tv_gameTendency_winOrlose)
        tv_gameTendency_onlyFun = view.findViewById(R.id.tv_gameTendency_onlyFun)
        tv_gameTendency_onlyWin = view.findViewById(R.id.tv_gameTendency_onlyWin)
        tv_gameTendency_voiceOk = view.findViewById(R.id.tv_gameTendency_voiceOk)
        tv_gameTendency_voiceNo = view.findViewById(R.id.tv_gameTendency_voiceNo)
        tv_gameTendency_onlyWomen = view.findViewById(R.id.tv_gameTendency_onlyWomen)
        tv_gameTendency_onlyMen = view.findViewById(R.id.tv_gameTendency_onlyMen)
        tv_gameTendency_womenOrmen = view.findViewById(R.id.tv_gameTendency_womenOrmen)

        tv_gameNames_LOL = view.findViewById(R.id.tv_gameNames_LOL)
        tv_gameNames_OverWatch = view.findViewById(R.id.tv_gameNames_OverWatch)
        tv_gameNames_BattleGround = view.findViewById(R.id.tv_gameNames_BattleGround)
        tv_gameNames_SuddenAttack = view.findViewById(R.id.tv_gameNames_SuddenAttack)
        tv_gameNames_FIFAOnline4 = view.findViewById(R.id.tv_gameNames_FIFAOnline4)
        tv_gameNames_LostArk = view.findViewById(R.id.tv_gameNames_LostArk)
        tv_gameNames_MapleStory = view.findViewById(R.id.tv_gameNames_MapleStory)
        tv_gameNames_Cyphers = view.findViewById(R.id.tv_gameNames_Cyphers)
        tv_gameNames_StarCraft = view.findViewById(R.id.tv_gameNames_StarCraft)
        tv_gameNames_DungeonandFighter = view.findViewById(R.id.tv_gameNames_DungeonandFighter)
//        tv_gameNames_DeadbyDaylight = view.findViewById(R.id.tv_gameNames_DeadbyDaylight)
//        tv_gameNames_StardewValley = view.findViewById(R.id.tv_gameNames_StardewValley)

        // modify button
        btn_modifyUserInfo = view.findViewById(R.id.btn_modifyUserInfo)
    }

    private fun getUserProfile() {

        iv_userPicture.setImageDrawable(resources.getDrawable(R.drawable.temp_img))
        tv_gameUserName.text = "한우먹으러갈래요"
        tv_userNickName.text = "RIT"
        tv_realKindness.text = "90.7%"
        NumberofAppraiser = 1235
        tv_estimatedKindness.text = "총 ${NumberofAppraiser}명이 판단했어요!"

        val ListofTendency = listOf(
            Games(tv_gameTendency_wantWin, true),
            Games(tv_gameTendency_winOrlose, false),
            Games(tv_gameTendency_onlyFun, true),
            Games(tv_gameTendency_onlyWin, false),
            Games(tv_gameTendency_voiceOk, true),
            Games(tv_gameTendency_voiceNo, false),
            Games(tv_gameTendency_onlyWomen, false),
            Games(tv_gameTendency_onlyMen, false),
            Games(tv_gameTendency_womenOrmen, true)
        )
        setTextView(ListofTendency)
        
        val ListofGames = listOf(
            Games(tv_gameNames_LOL, true),
            Games(tv_gameNames_OverWatch, true),
            Games(tv_gameNames_BattleGround, false),
            Games(tv_gameNames_SuddenAttack, false),
            Games(tv_gameNames_FIFAOnline4, false),
            Games(tv_gameNames_LostArk, false),
            Games(tv_gameNames_MapleStory, false),
            Games(tv_gameNames_Cyphers, true),
            Games(tv_gameNames_StarCraft, false),
            Games(tv_gameNames_DungeonandFighter, false)
//            Games(tv_gameNames_DeadbyDaylight, true),
//            Games(tv_gameNames_StardewValley, true)
        )
        setTextView(ListofGames)
        
    }

    private fun setTextView(list : List<Games>) {
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