package com.naram.party_project.data.remote.model

data class Profile(
    val email : String,
    val picture : String?,
    val nickName : String,
    val gameName : String?,
    val gender : String,
    val selfPR : String?,
    val gameTendency : List<String>,
    val gameNamesBoolean : List<Boolean>?,
    val gameNamesInt : List<Int>?,
)