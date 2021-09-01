package com.naram.party_project.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.naram.party_project.model.User

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

    @Query("UPDATE user SET picture_uri = :picture where email like :email")
    fun updatePicture(email : String, picture : String)

}