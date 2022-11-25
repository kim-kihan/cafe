package com.ssafy.smartstore.dto


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class OrderDetail(var orderId: Int = 0, var productId: Int = 0, var quantity: Int = 0) : Serializable {

    var id = 0

    constructor(id: Int, orderId: Int, productId: Int, quantity: Int) : this(orderId, productId, quantity) {
        this.id = id
    }
}
