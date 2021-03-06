package com.naram.party_project.util

class Const {
    companion object {
        const val IP_ADDRESS = "IP ADDRESS"

        // picture
        const val REQUEST_GALLERY = 1000
        const val REQUEST_SUCCESS = 1001

        var MODIFY_USER_PROFILE = false
        var UPDATE_USER_LIST = false

        const val TAG_TENDENCY_PURPOSE = "purpose"
        const val TAG_TENDENCY_VOICE = "voice"
        const val TAG_TENDENCY_PREFERRED_GENDER_MEN = "men"
        const val TAG_TENDENCY_PREFERRED_GENDER_WOMEN = "women"
        const val TAG_TENDENCY_GAME_MODE = "game_mode"

        // Firebase
        const val FIREBASE_USERS = "users"
        const val FIREBASE_USER = "user"
        const val FIREBASE_USER_NAME = "name"
        const val FIREBASE_USER_EMAIL = "email"
        const val FIREBASE_USER_PASSWORD = "password"
        const val FIREBASE_USER_GENDER = "gender"
        const val FIREBASE_USER_GAME_NAME = "game_name"
        const val FIREBASE_USER_PICTURE = "picture"
        const val FIREBASE_USER_SELF_PR = "self_pr"

        const val FIREBASE_TENDENCY = "tendency"
        const val FIREBASE_TENDENCY_PURPOSE = "purpose"
        const val FIREBASE_TENDENCY_VOICE = "voice"
        const val FIREBASE_TENDENCY_PREFERRED_GENDER_MEN = "men"
        const val FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN = "women"
        const val FIREBASE_TENDENCY_GAME_MODE = "game_mode"

        const val FIREBASE_GAME = "game"
        const val FIREBASE_GAME_0_LOL = "game0"
        const val FIREBASE_GAME_1_OVER_WATCH = "game1"
        const val FIREBASE_GAME_2_BATTLE_GROUND = "game2"
        const val FIREBASE_GAME_3_SUDDEN_ATTACK = "game3"
        const val FIREBASE_GAME_4_FIFA_ONLINE_4 = "game4"
        const val FIREBASE_GAME_5_LOST_ARK = "game5"
        const val FIREBASE_GAME_6_MAPLE_STORY = "game6"
        const val FIREBASE_GAME_7_CYPHERS = "game7"
        const val FIREBASE_GAME_8_STAR_CRAFT = "game8"
        const val FIREBASE_GAME_9_DUNGEON_AND_FIGHTER = "game9"

        const val FIREBASE_PARTY = "party"
        const val FIREBASE_PARTY_WAIT = "wait"

        const val FIREBASE_FRIEND = "friend"
        const val FIREBASE_FRIEND_YES = "yes"

        const val FIREBASE_CHATTING = "chatting"
        const val FIREBASE_CHATTING_USERS = "users"
        const val FIREBASE_CHATTING_MESSAGE = "message"
        const val FIREBASE_CHATTING_ISREAD = "isRead"

        val FIREBASE_TABLE_NAMES = arrayListOf(
            FIREBASE_USERS,
            FIREBASE_USER,
            FIREBASE_TENDENCY,
            FIREBASE_GAME,
            FIREBASE_PARTY,
            FIREBASE_FRIEND,
            FIREBASE_CHATTING
        )

        fun processingTendency(map : Map<String, String>) : List<String> {
            val list = mutableListOf<String>()
            map.forEach { (s, s2) ->
                if(s == TAG_TENDENCY_PURPOSE) {
                    when(s2) {
                        "1" -> list.add("????????????")
                        "0" -> list.add("???????????? X")
                    }
                }
                if(s == TAG_TENDENCY_VOICE) {
                    when(s2) {
                        "1" -> list.add("???????????? O")
                        "0" -> list.add("???????????? X")
                    }
                }
                if(s == TAG_TENDENCY_PREFERRED_GENDER_MEN) {
                    when(s2) {
                        "1" -> list.add("?????? Only")
                        "0" -> list.add("?????? Only")
                    }
                }
                if(s == TAG_TENDENCY_PREFERRED_GENDER_WOMEN) {
                    when(s2) {
                        "1" -> list.add("?????? Only")
                        "0" -> list.add("?????? Only")
                    }
                }
                if(list.contains("?????? Only") && list.contains("?????? Only")) {
                    list.remove("?????? Only")
                    list.remove("?????? Only")
                    list.add("???????????? X")
                }
                if(s == TAG_TENDENCY_GAME_MODE) {
                    when(s2) {
                        "1" -> list.add("??????")
                        "0" -> list.add("??????")
                    }
                }
            }

            return list

        }

        fun processingTendency(list : MutableList<String>?) : Map<String, String> {
            val map = mutableMapOf<String, String>()
            list?.forEach {
                when(it) {
                    "????????????" -> map["purpose"] = "1"
                    "???????????? X" -> map["purpose"] = "0"
                    "???????????? O" -> map["voice"] = "1"
                    "???????????? X" -> map["voice"] = "0"
                    "???????????? X" -> {
                        map["men"] = "1"
                        map["women"] = "1"
                    }
                    "?????? Only" -> {
                        map["women"] = "1"
                        map["men"] = "0"
                    }
                    "?????? Only" -> {
                        map["men"] = "1"
                        map["women"] = "0"
                    }
                    "??????" -> map["game_mode"] = "1"
                    "??????" -> map["game_mode"] = "0"
                }
            }

            return map

        }

        fun ListToMap(list : MutableList<String>?) : Map<String, String> {
            val map = mutableMapOf<String, String>()
            list?.forEach {
                when(it) {
                    "????????????" -> map["purpose"] = it
                    "???????????? X" -> map["purpose"] = it
                    "???????????? O" -> map["voice"] = it
                    "???????????? X" -> map["voice"] = it
                    "???????????? X" -> map["preferred_gender"] = it
                    "?????? Only" -> map["preferred_gender"] = it
                    "?????? Only" -> map["preferred_gender"] = it
                    "??????" -> map["game_mode"] = it
                    "??????" -> map["game_mode"] = it
                }
            }

            return map

        }

    }
}