package com.ssafy.smartstore.dto

import java.io.Serializable

data class Search(var userId: String, var content: String?) : Serializable {
    var id = 0
}
