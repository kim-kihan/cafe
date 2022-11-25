package com.ssafy.smartstore.database

import com.ssafy.smartstore.dto.Search
import com.ssafy.smartstore.dto.Stamp
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SearchDao {
    @POST("rest/search")
    fun insert(@Body body: Search): Call<Int>

    @GET("rest/search/{userId}")
    fun select(@Path("userId") id: String): Call<List<Search>>
}