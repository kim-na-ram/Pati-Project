package com.naram.party_project.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDAO {

    @Query("SELECT * FROM user where email like :email")
    fun getUserInfo(email : String): List<User>

    @Query("SELECT * FROM user")
    fun getUserInfo(): List<User>

    @Insert
    fun insertUserInfo(user: User)

    @Query("DELETE FROM user")
    fun deleteAll()

    @Query("UPDATE user SET picture=:picture, user_name=:user_name, game_name=:game_name, self_pr=:self_pr where email like :email")
    fun updateUserInfo(email : String, picture: String?, user_name : String, game_name : String?, self_pr : String?)

}