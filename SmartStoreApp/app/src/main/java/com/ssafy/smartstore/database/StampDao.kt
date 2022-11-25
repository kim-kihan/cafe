package com.ssafy.smartstore.database

import com.ssafy.smartstore.dto.Stamp
import retrofit2.Call
import retrofit2.http.*

interface StampDao {
    @POST("rest/stamp")
    fun insert(@Body body: Stamp): Call<Int>

    @GET("rest/stamp/{userId}")
    fun selectByUserId(@Path("userId") id: String): Call<List<Stamp>>
}