package com.naram.party_project.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity
data class User(
    @PrimaryKey val email: String,
    @ColumnInfo(name = "picture") val picture : String?,
    @ColumnInfo(name = "picture_uri") val picture_uri: String?,
    @ColumnInfo(name = "user_name") val user_name: String,
    @ColumnInfo(name = "game_name") val game_name: String?,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "self_pr") val self_pr: String?,
    @ColumnInfo(name = "tendency0") val tendency0: String,
    @ColumnInfo(name = "tendency1") val tendency1: String,
    @ColumnInfo(name = "tendency2") val tendency2: String,
    @ColumnInfo(name = "tendency3") val tendency3: String,
    @ColumnInfo(name = "game0") val game0: Boolean,
    @ColumnInfo(name = "game1") val game1: Boolean,
    @ColumnInfo(name = "game2") val game2: Boolean,
    @ColumnInfo(name = "game3") val game3: Boolean,
    @ColumnInfo(name = "game4") val game4: Boolean,
    @ColumnInfo(name = "game5") val game5: Boolean,
    @ColumnInfo(name = "game6") val game6: Boolean,
    @ColumnInfo(name = "game7") val game7: Boolean,
    @ColumnInfo(name = "game8") val game8: Boolean,
    @ColumnInfo(name = "game9") val game9: Boolean,
)
