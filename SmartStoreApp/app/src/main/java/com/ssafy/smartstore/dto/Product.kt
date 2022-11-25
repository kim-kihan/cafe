package com.ssafy.smartstore.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Product(var name: String?, var type: String, var price: Int, var img: String, var date: String, var views:Int) : Serializable {

    var id = 0

    constructor(id: Int, name: String, type: String, price: Int, img: String, date: String, views: Int)  : this(name, type, price, img, date, views) {
        this.id = id
    }

}