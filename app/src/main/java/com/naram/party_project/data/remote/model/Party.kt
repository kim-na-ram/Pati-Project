package com.naram.party_project.data.remote.model

import android.graphics.Bitmap

data class Party(
    val status : String,
    val message : String,
    val email : String?,
    val user_name : String?,
    val gender : String?,
    val game_name : String?,
    val self_pr : String?,
    val picture : String?,
    var bitmap : Bitmap?,
    val game0 : String?,
    val game1 : String?,
    val game2 : String?,
    val game3 : String?,
    val game4 : String?,
    val game5 : String?,
    val game6 : String?,
    val game7 : String?,
    val game8 : String?,
    val game9 : String?
)