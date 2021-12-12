package com.naram.party_project.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.viewbinding.ViewBinding
import com.naram.party_project.AppDatabase

abstract class BaseActivity<B: ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B
) : AppCompatActivity() {
    private var _binding: B? = null
    val binding
        get() = _binding!!

    lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingFactory(layoutInflater)
        setContentView(binding.root)

        createDB()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun createDB() {
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "userDB"
        )
            .build()
    }
}