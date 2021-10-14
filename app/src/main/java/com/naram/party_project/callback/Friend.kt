package com.naram.party_project.callback

import android.graphics.Bitmap

data class Friend(
    val status : String,
    val message : String,
    val email : String?,
    val user_name : String?,
    val picture : String?,
    var bitmap : Bitmap?
)