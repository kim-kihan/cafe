package com.ssafy.smartstore.firebase
import retrofit2.Call
import retrofit2.http.*

interface FirebaseTokenService {
    // Token 정보 서버로 전송
    @POST("token")
    fun uploadToken(@Query("token") token: String): Call<String>
}