package com.ssafy.smartstore.database

import androidx.room.*
import com.ssafy.smartstore.dto.OrderDetail
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderDetailDao {

    @GET("rest/order/orderdetail/{orderId}")
    fun select(@Path("orderId")orderId: Int): Call<List<OrderDetail>>

    @POST("rest/order/orderdetail")
    fun insert(@Body body: OrderDetail): Call<Boolean>
}