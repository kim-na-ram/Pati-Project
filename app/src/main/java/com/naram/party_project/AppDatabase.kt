package com.naram.party_project

import androidx.room.Database
import androidx.room.RoomDatabase
import com.naram.party_project.dao.GameDAO
import com.naram.party_project.dao.TendencyDAO
import com.naram.party_project.dao.UserDAO
import com.naram.party_project.model.Game
import com.naram.party_project.model.Tendency
import com.naram.party_project.model.User

@Database(entities = [User::class, Tendency::class, Game::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun tendencyDAO(): TendencyDAO

    abstract fun gameDAO(): GameDAO

}