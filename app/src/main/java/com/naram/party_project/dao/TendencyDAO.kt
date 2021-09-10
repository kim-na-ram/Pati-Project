package com.naram.party_project.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.naram.party_project.model.Tendency

@Dao
interface TendencyDAO {

    @Query("SELECT * FROM tendency where email like :email")
    fun getTendencyInfo(email : String): List<Tendency>

    @Query("SELECT * FROM tendency")
    fun getTendencyInfo(): List<Tendency>

    @Insert
    fun insertTendencyInfo(tendency: Tendency)

    @Query("INSERT INTO tendency(email) values(:email)")
    fun insertUserTendencyInfo(email : String)

    @Query("DELETE FROM tendency")
    fun deleteAll()

    @Query("UPDATE tendency SET purpose=:purpose, game_mode=:game_mode, voice=:voice, preferred_gender=:preferred_gender where email like :email")
    fun updateUserSimpleInfo(email: String, purpose: String, voice: String, preferred_gender: String, game_mode: String)

}