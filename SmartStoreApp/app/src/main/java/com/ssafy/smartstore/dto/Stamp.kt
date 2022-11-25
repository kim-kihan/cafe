package com.ssafy.smartstore.dto

import java.io.Serializable

data class Stamp(var userId: String?, var orderId: Int, var quantity: Int) : Serializable {
    var id = -1

    constructor(id: Int, userId: String, orderId: Int, quantity: Int) : this(userId, orderId, quantity) {
        this.id = id
    }

    override fun toString(): String {
        return "물품 : $userId -> 수량 : $orderId"
    }
}