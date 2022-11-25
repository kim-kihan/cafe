package com.ssafy.smartstore.dto

import java.io.Serializable

data class OrderProduct(val productId: String, val name:String, val price:String, var count:String, val img: String):
    Serializable
