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
//    val MIGRATION_2_3 = object : Migration(2, 3) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("DROP TABLE user")
//
//            database.execSQL(
//                "CREATE TABLE user (" +
//                        "email TEXT PRIMARY KEY NOT NULL, " +
//                        "picture TEXT, " +
//                        "picture_uri TEXT, " +
//                        "user_name TEXT NOT NULL, " +
//                        "game_name TEXT, " +
//                        "gender TEXT NOT NULL, " +
//                        "self_pr TEXT)"
//            )
//
//            database.execSQL(
//                "CREATE TABLE tendency (" +
//                        "email TEXT PRIMARY KEY NOT NULL, " +
//                        "purpose TEXT DEFAULT '승리지향' NOT NULL," +
//                        "voice TEXT DEFAULT '보이스톡 O' NOT NULL," +
//                        "preferred_gender TEXT DEFAULT '성별상관 X' NOT NULL," +
//                        "game_mode TEXT DEFAULT '즐겜' NOT NULL," +
//                        "CONSTRAINT email_pk FOREIGN KEY(email) REFERENCES User(email) ON DELETE CASCADE ON UPDATE CASCADE)"
//            )
//
//            database.execSQL(
//                "CREATE TABLE game (" +
//                        "email TEXT PRIMARY KEY NOT NULL, " +
//                        "game0 INTEGER DEFAULT 0 NOT NULL," +
//                        "game1 INTEGER DEFAULT 0 NOT NULL," +
//                        "game2 INTEGER DEFAULT 0 NOT NULL," +
//                        "game3 INTEGER DEFAULT 0 NOT NULL," +
//                        "game4 INTEGER DEFAULT 0 NOT NULL," +
//                        "game5 INTEGER DEFAULT 0 NOT NULL," +
//                        "game6 INTEGER DEFAULT 0 NOT NULL," +
//                        "game7 INTEGER DEFAULT 0 NOT NULL," +
//                        "game8 INTEGER DEFAULT 0 NOT NULL," +
//                        "game9 INTEGER DEFAULT 0 NOT NULL," +
//                        "CONSTRAINT email_pk FOREIGN KEY(email) REFERENCES User(email) ON DELETE CASCADE ON UPDATE CASCADE)"
//            )
//        }
//    }

}