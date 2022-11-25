package com.ssafy.smartstore.dto

import java.io.Serializable
import java.util.*

data class Order(var userId: String?, var orderTable: String, var orderTime: String, var completed: String) : Serializable {

    var id = 0

    constructor(id: Int, userId: String, order_table: String, regDate: String, completed: String) : this(userId, order_table, regDate, completed) {
        this.id = id
    }

    override fun toString(): String {
        return "물품 : $userId -> 수량 : $orderTable"
    }
}