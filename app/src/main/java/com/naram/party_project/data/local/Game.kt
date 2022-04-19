package com.naram.party_project.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = User::class,
        parentColumns = arrayOf("email"),
        childColumns = arrayOf("email"),
        onDelete = CASCADE,
        onUpdate = CASCADE)))
data class Game(
    @PrimaryKey val email: String,
    @ColumnInfo(name = "game0", defaultValue = "0") val game0: Int,
    @ColumnInfo(name = "game1", defaultValue = "0") val game1: Int,
    @ColumnInfo(name = "game2", defaultValue = "0") val game2: Int,
    @ColumnInfo(name = "game3", defaultValue = "0") val game3: Int,
    @ColumnInfo(name = "game4", defaultValue = "0") val game4: Int,
    @ColumnInfo(name = "game5", defaultValue = "0") val game5: Int,
    @ColumnInfo(name = "game6", defaultValue = "0") val game6: Int,
    @ColumnInfo(name = "game7", defaultValue = "0") val game7: Int,
    @ColumnInfo(name = "game8", defaultValue = "0") val game8: Int,
    @ColumnInfo(name = "game9", defaultValue = "0") val game9: Int
)
