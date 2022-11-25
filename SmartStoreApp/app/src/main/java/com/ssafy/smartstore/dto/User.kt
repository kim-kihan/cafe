package com.ssafy.smartstore.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class User(var id: String = "id", var name: String = "name",
                var pass: String = "pass", var stamps: Int = -1, var age: String =  "age", var gender: String =  "gender") : Serializable{

}