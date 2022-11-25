package com.ssafy.smartstore.database

import androidx.room.*
import com.ssafy.smartstore.dto.Order
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Query

interface OrderDao {
    @GET("rest/order")
    fun selectAll(): Call<List<Order>>

    @GET("rest/order/byUser")
    fun getLastMonthOrder(@Query("id") id: String): Call<List<Map<String, Object>>>

    @GET("rest/order/{orderId}")
    fun getOrderDetail(@Path("orderId") orderId: Int): Call<List<Map<String, Object>>>

    @POST("rest/order")
    fun makeOrder(@Body body: Order): Call<Int>

}