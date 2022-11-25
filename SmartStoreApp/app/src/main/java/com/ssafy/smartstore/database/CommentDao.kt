package com.ssafy.smartstore.database

import androidx.room.*
import com.ssafy.smartstore.dto.Comment
import retrofit2.Call
import retrofit2.http.*

interface CommentDao {

    @GET("rest/comment")
    fun selectAll(): Call<List<Comment>>

    @GET("rest/comment/{productId}")
    fun selectByProduct(@Path("productId")productID: Int): Call<List<Comment>>

    @POST("rest/comment")
    fun insert(@Body body: Comment): Call<Boolean>

    @PUT("rest/comment")
    fun update(@Body body: Comment): Call<Boolean>

    @DELETE("rest/comment/{id}")
    fun delete(@Path("id")id: Int): Call<Boolean>
}