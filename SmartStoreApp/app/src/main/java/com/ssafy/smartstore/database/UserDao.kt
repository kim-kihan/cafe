package com.ssafy.smartstore.database

import androidx.room.*
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.User
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Query

interface UserDao {
    @POST("rest/user/login")
    fun login(@Body body: User): Call<User>

    @POST("rest/user")
    fun insert(@Body body: User): Call<Boolean>

    @GET("rest/user/stamps")
    fun checkStamps(@Query("id") id: String): Call<User>

    @GET("rest/user/isUsed")
    fun checklogin(@Query("id") id: String): Call<Boolean>

    @GET("rest/user/age")
    fun checkAge(@Query("id") id: String): Call<User>

    @GET("rest/user/gender")
    fun checkGender(@Query("id") id: String): Call<User>

    @PUT("rest/user")
    fun update(@Body body: User): Call<Int>

}