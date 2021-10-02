package com.naram.party_project

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.databinding.FragmentShowprofileBinding
import com.nex3z.flowlayout.FlowLayout

class Fragment_Showprofile : Fragment() {

    private val TAG = "Showprofile"

    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private var _binding: FragmentShowprofileBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowprofileBinding.inflate(inflater, container, false)
        val view = binding.root

        Log.d(TAG, "onCreateView")

        initViews()
        setUserProfile()

        return view
    }

    private fun initViews() {

        binding.btnSendMessage.setOnClickListener {
            // TODO 채팅 보내기
        }

        binding.btnRequestParty.setOnClickListener {
            // TODO 파티 요청 보내기
        }

    }

    private fun setUserProfile() {
        val profile = mainActivity.getProfile()
        profile?.let { profile ->
            val name = profile.user_name
            binding.tvPartyUserName.text = name

            val selfpr = profile.self_pr
            selfpr?.let {
                binding.tvPartyLeftQuote.visibility = View.VISIBLE
                binding.tvPartyRightQuote.visibility = View.VISIBLE
                binding.tvPartyUserPR.visibility = View.VISIBLE
                binding.tvPartyUserPR.text = it
            }

            val picture = profile.picture
            picture?.let {
                uploadImageFromCloud(it)
            }

            val purpose = profile.purpose.toInt()
            purpose?.let {
                var str = if(it == 1) "승리지향" else "승패상관 x"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            val voice = profile.voice.toInt()
            voice?.let {
                var str = if(it == 1) "보이스톡 O" else "보이스톡 X"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            val game_mode = profile.game_mode.toInt()
            game_mode?.let {
                var str = if(it == 1) "즐겜" else "빡겜"
                makeTextView(str!!, binding.flPartyShowTendency)
            }
            val men = profile.men.toInt()
            val women = profile.women.toInt()
            men?.let { m ->
                var str : String? = null
                women?.let { w ->
                    if(m == 1 && w == 1) str = "성별상관 X"
                    else if(m == 1 && w == 0) str = "남성 Only"
                    else if (m == 0 && w == 1) str = "여성 Only"
                    makeTextView(str!!, binding.flPartyShowTendency)
                }
            }

            if (profile.game0?.toInt() == 1) binding.tvPartyGame0.visibility = View.VISIBLE
            if (profile.game1?.toInt() == 1) binding.tvPartyGame1.visibility = View.VISIBLE
            if (profile.game2?.toInt() == 1) binding.tvPartyGame2.visibility = View.VISIBLE
            if (profile.game3?.toInt() == 1) binding.tvPartyGame3.visibility = View.VISIBLE
            if (profile.game4?.toInt() == 1) binding.tvPartyGame4.visibility = View.VISIBLE
            if (profile.game5?.toInt() == 1) binding.tvPartyGame5.visibility = View.VISIBLE
            if (profile.game6?.toInt() == 1) binding.tvPartyGame6.visibility = View.VISIBLE
            if (profile.game7?.toInt() == 1) binding.tvPartyGame7.visibility = View.VISIBLE
            if (profile.game8?.toInt() == 1) binding.tvPartyGame8.visibility = View.VISIBLE
            if (profile.game9?.toInt() == 1) binding.tvPartyGame9.visibility = View.VISIBLE
        }
    }

    private fun uploadImageFromCloud(path: String) {
        val imagesRef = Firebase.storage.reference.child(path)

        imagesRef.downloadUrl.addOnCompleteListener { task ->
            task.addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.app_logo)
                    .override(
                        binding.ivPartyUserPicture.width,
                        binding.ivPartyUserPicture.height
                    )
                    .into(binding.ivPartyUserPicture)
            }.addOnFailureListener {
                Toast.makeText(
                    binding.root.context,
                    "사진을 불러오는데 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun makeTextView(text: String, layout : FlowLayout) {
        val gameTextView = TextView(this.requireContext())
        gameTextView.text = text
        gameTextView.setTextColor(resources.getColor(R.color.white, null))
        gameTextView.background = resources.getDrawable(R.drawable.textview_rounded_activated, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gameTextView.typeface = resources.getFont(R.font.nanumsquarebold)
        }
        gameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.toFloat())
        gameTextView.setPadding(3.dpToPixels(requireContext()), 1.dpToPixels(requireContext()), 3.dpToPixels(requireContext()), 1.dpToPixels(requireContext()))

        LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginEnd = 5.dpToPixels(requireContext())
            bottomMargin = 5.dpToPixels(requireContext())

            gameTextView.layoutParams = this
        }

        layout.addView(gameTextView)
    }

    fun Int.dpToPixels(context: Context):Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
    ).toInt()

}