package com.ssafy.smartstore.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Comment(var userId: String?, var productId: Int, var rating: Float, var comment: String) : Serializable {

    var id = 0

}
