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
}