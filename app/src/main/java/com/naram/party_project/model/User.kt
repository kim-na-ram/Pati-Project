package com.naram.party_project.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val email: String,
    @ColumnInfo(name = "picture") val picture : String?,
    @ColumnInfo(name = "picture_uri") val picture_uri: String?,
    @ColumnInfo(name = "user_name") val user_name: String,
    @ColumnInfo(name = "game_name") val game_name: String?,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "self_pr") val self_pr: String?
)
