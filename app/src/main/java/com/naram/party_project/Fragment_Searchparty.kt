package com.naram.party_project

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.naram.party_project.callback.Party
import com.naram.party_project.databinding.FragmentSearchpartyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Fragment_Searchparty : Fragment() {

    private val TAG = "Searchparty_Fragment"

    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private val email = FirebaseAuth.getInstance().currentUser?.email

    private var _binding: FragmentSearchpartyBinding? = null
    private val binding get() = _binding!!

    lateinit var userListAdapter : UserListAdapter
//    lateinit var layoutManager : RecyclerView.LayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchpartyBinding.inflate(inflater, container, false)
        val view = binding.root

        createDB()
        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val datas = mutableListOf<Party>(
//            Party(
//                email = "snrneh3@naver.com", user_name = "나람", gender = "F",
//                game_name = null, self_pr = "사이퍼즈 죽을 때까지 하자고.", picture = "snrneh3@naver.com/profile.jpg",
//                game0 = "1", game1 = "1", game2 = "1", game3 = "1", game4 = "1",
//                game5 = "1", game6 = "1", game7 = "1", game8 = "1", game9 = "1"
//            ),
//            Party(
//                email = "tlsdmswjs3@gmail.com", user_name = "RIT", gender = "F",
//                game_name = null, self_pr = null, picture = "hanu3542@gmail.com/profile.jpg",
//                game0 = "1", game1 = "1", game2 = "0", game3 = "0", game4 = "0",
//                game5 = "0", game6 = "0", game7 = "1", game8 = "0", game9 = "0"
//            )
//        )

        userListAdapter = UserListAdapter()
        userListAdapter.setHasStableIds(true)
        binding.rvPartyUserList.adapter = userListAdapter

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

        binding.progessBar.incrementProgressBy(20)

        val retrofit = RetrofitClient.getInstance()

        val server = retrofit.create(UserAPI::class.java)

        server.getAllUser(email!!).enqueue(object : Callback<List<Party>> {
            override fun onResponse(
                call: Call<List<Party>>,
                response: Response<List<Party>>
            ) {
                if(response.isSuccessful) {
                    Log.d(TAG, "성공 : ${response.body().toString()}")
                    val list : List<Party> = response.body()!!
                    setRecyclerView(list)
                } else {
                    Log.d(TAG, "실패 : ${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<List<Party>>, t: Throwable) {
                Log.d(TAG, "실패 : ${t.localizedMessage}")
            }
        })

//        server.getAllUser(email!!).enqueue(object : Callback<String> {
//            override fun onResponse(
//                call: Call<String>,
//                response: Response<String>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "성공 : ${response.body().toString()}")
//                    setRecyclerView(response)
//                } else {
//                    Log.d(TAG, "실패 : ${response.errorBody().toString()}")
//                }
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.d(TAG, "실패 : ${t.localizedMessage}")
//            }
//        })

    }

    private fun setRecyclerView(list: List<Party>) {

        val datas = mutableListOf<Party>()

        datas.apply {
            list.forEach {
                this.add(
                    Party(
                        it.email, it.user_name, it.gender,
                        it.game_name, it.self_pr, it.picture,
                        it.game0, it.game1, it.game2, it.game3, it.game4,
                        it.game5, it.game6, it.game7, it.game8, it.game9
                    )
                )
            }
        }

        userListAdapter.refreshRecyclerView(datas)
        binding.rvPartyUserList.visibility = View.VISIBLE
        binding.progessBar.visibility = View.GONE

    }
}