package com.naram.party_project.model

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
data class Tendency(
    @PrimaryKey val email: String,
    @ColumnInfo(name = "purpose", defaultValue = "승리지향") val purpose: String,
    @ColumnInfo(name = "voice", defaultValue = "보이스톡 O") val voice: String,
    @ColumnInfo(name = "preferred_gender", defaultValue = "성별상관 X") val preferred_gender: String,
    @ColumnInfo(name = "game_mode", defaultValue = "즐겜") val game_mode: String
    )
