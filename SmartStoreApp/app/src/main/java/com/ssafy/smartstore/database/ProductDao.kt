package com.ssafy.smartstore.database

import androidx.room.*
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.dto.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductDao {
    @GET("rest/product")
    fun getProductList(): Call<List<Product>>

    @GET("rest/product/{productId}")
    fun getProduct(@Path("productId")id: Int): Call<Product>

    @PUT("rest/product")
    fun update(@Body body: Product): Call<Int>
}