package com.naram.party_project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Tendency::class, Game::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun tendencyDAO(): TendencyDAO

    abstract fun gameDAO(): GameDAO

}