package com.naram.party_project

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.naram.party_project.dao.UserDAO
import com.naram.party_project.model.User

@Database(entities = [User::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO() : UserDAO

    // Migration 코드
//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE 'user' ADD COLUMN 'picture_uri' TEXT")
//        }
//    }

}