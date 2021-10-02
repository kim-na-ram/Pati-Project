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

class Fragment_Searchparty : Fragment() {

    private val TAG = "Searchparty"

    private lateinit var mainActivity: MainActivity

    private lateinit var db: AppDatabase

    private val email = FirebaseAuth.getInstance().currentUser?.email
    private lateinit var list : List<Party>

    private var _binding: FragmentSearchpartyBinding? = null
    private val binding get() = _binding!!

    lateinit var userListAdapter : UserListAdapter

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

        Log.d(TAG, "onCreateView")

        list = mainActivity.getList()

        createDB()
        initViews()

        return view
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")

    }

    override fun onPause() {
        super.onPause()

        mainActivity.disappearFragment()

        Log.d(TAG, "onPause")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

        userListAdapter = UserListAdapter(this.requireContext(), list as MutableList<Party>) {
            if(mainActivity.getProfile() == null)
                mainActivity.getPartyUserProfile(it.email)
            else {
                mainActivity.disappearFragment()
                mainActivity.getPartyUserProfile(it.email)
            }
        }
        userListAdapter.setHasStableIds(true)
        binding.rvPartyUserList.visibility = View.VISIBLE
        binding.rvPartyUserList.adapter = userListAdapter
        binding.progessBar.visibility = View.GONE

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

    }

}