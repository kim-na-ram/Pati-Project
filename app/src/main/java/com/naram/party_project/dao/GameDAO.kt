package com.naram.party_project.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.naram.party_project.model.Game

@Dao
interface GameDAO {

    @Query("SELECT * FROM game where email like :email")
    fun getGameInfo(email : String): List<Game>

    @Query("SELECT * FROM game")
    fun getGameInfo(): List<Game>

    @Insert
    fun insertGameInfo(game : Game)

    @Query("INSERT INTO game(email) VALUES (:email)")
    fun insertUserGameInfo(email : String)

    @Query("UPDATE game SET game0=:game0, game1=:game1, game2=:game2, game3=:game3, game4=:game4, game5=:game5, game6=:game6, game7=:game7, game8=:game8, game9=:game9 WHERE email=:email")
    fun updateGameInfo(email : String, game0 : Int, game1 : Int, game2 : Int, game3 : Int, game4 : Int, game5 : Int, game6 : Int, game7 : Int, game8 : Int, game9 : Int)
}