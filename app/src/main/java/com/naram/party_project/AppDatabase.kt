package com.naram.party_project

import androidx.room.Database
import androidx.room.RoomDatabase
import com.naram.party_project.dao.GameDAO
import com.naram.party_project.dao.TendencyDAO
import com.naram.party_project.dao.UserDAO
import com.naram.party_project.model.Game
import com.naram.party_project.model.Tendency
import com.naram.party_project.model.User

@Database(entities = [User::class, Tendency::class, Game::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun tendencyDAO(): TendencyDAO

    abstract fun gameDAO(): GameDAO

    // Migration 코드
//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE 'user' ADD COLUMN 'picture_uri' TEXT")
//        }
//    }

}