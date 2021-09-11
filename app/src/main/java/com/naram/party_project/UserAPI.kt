package com.naram.party_project

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {
    @FormUrlEncoded
    @POST("retrofitUserSelect.php")
    fun getUser(
        @Field("email") email: String
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("retrofitUserInsert.php")
    fun putUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("user_name") user_name: String,
        @Field("gender") gender: String,
        @Field("picture") picture: String?
    ) : Call<String>

    @FormUrlEncoded
    @POST("retrofitUserUpdate.php")
    fun updateUser(
        @Field("email") email: String,
        @Field("picture") picture: String?,
        @Field("user_name") user_name: String,
        @Field("game_name") game_name: String?,
        @Field("self_pr") self_pr: String?,
        @Field("purpose") purpose: String,
        @Field("voice") voice: String,
        @Field("men") men: String,
        @Field("women") women: String,
        @Field("game_mode") game_mode: String,
        @Field("game0") game0 : String,
        @Field("game1") game1: String,
        @Field("game2") game2: String,
        @Field("game3") game3: String,
        @Field("game4") game4: String,
        @Field("game5") game5: String,
        @Field("game6") game6: String,
        @Field("game7") game7: String,
        @Field("game8") game8: String,
        @Field("game9") game9: String,
    ) : Call<String>
}