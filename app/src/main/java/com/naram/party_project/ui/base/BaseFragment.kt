package com.naram.party_project.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.viewbinding.ViewBinding
import com.facebook.stetho.Stetho
import com.naram.party_project.data.local.AppDatabase

abstract class BaseFragment<B: ViewBinding>: Fragment() {

    private var _binding: B? = null
    val binding
        get() = _binding!!

    lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentBinding()

        Stetho.initializeWithDefaults(context)

        createDB()

        return binding.root
    }

    abstract fun getFragmentBinding(): B
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
}