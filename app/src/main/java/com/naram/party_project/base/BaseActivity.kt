package com.naram.party_project.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        // Migration 코드
//        val MIGRATION_3_4 = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//
//                database.execSQL(
//                    "CREATE TABLE user_new (" +
//                            "email TEXT PRIMARY KEY NOT NULL, " +
//                            "picture TEXT, " +
//                            "user_name TEXT NOT NULL, " +
//                            "game_name TEXT, " +
//                            "gender TEXT NOT NULL, " +
//                            "self_pr TEXT)"
//                )
//
//                database.execSQL("DROP TABLE user")
//
//                database.execSQL("ALTER TABLE user_new RENAME TO user")
//            }
//        }

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "userDB"
        )
            .build()
    }
}