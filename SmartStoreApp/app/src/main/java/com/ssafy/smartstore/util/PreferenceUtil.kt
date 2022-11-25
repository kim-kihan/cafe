package com.ssafy.smartstore.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstore.dto.MessageDTO
import org.json.JSONArray
import java.lang.reflect.Type


class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("notice", Context.MODE_PRIVATE)
    var gson = Gson()

    // 값 체크
    fun checkString(key: String, defValue: String): Boolean {
        var json: String? = prefs.getString(key, defValue)
        var type: Type = object : TypeToken<ArrayList<MessageDTO?>?>() {}.type
        Log.d("TAG", "checkStringaa: $json")
        Log.d("TAG", "checkStringaa: $type")
        if(json.equals("none")){
            return false
        } else{
            return true
        }
    }

    // 불러오기
    fun getString(key: String, defValue: String): ArrayList<MessageDTO> {
        var json: String? = prefs.getString(key, defValue)
        var type: Type = object : TypeToken<ArrayList<MessageDTO?>?>() {}.type
        var arrayList: ArrayList<MessageDTO> = gson.fromJson(json, type)
        return arrayList
    }

    // 덮어쓰기
    fun setString(key: String, values: ArrayList<MessageDTO>) {
        prefs.edit().remove(key).apply()

        val editor = prefs.edit()
        val gsons = GsonBuilder().create()
        for(i in 0..values.size - 1){
            val data = values[i]
            val tempArray = ArrayList<MessageDTO>()
            val groupListType: Type = object : TypeToken<ArrayList<MessageDTO?>?>(){}.type
            val prev = prefs.getString("messageList", "none")
            if(prev!="none"){ //데이터가 비어있지 않다면?
                if(prev!="[]" || prev!="")tempArray.addAll(gsons.fromJson(prev,groupListType))
                tempArray.add(data)
                val strList = gsons.toJson(tempArray,groupListType)
                editor.putString("messageList",strList)
            }else{
                tempArray.add(data)
                val strList = gsons.toJson(tempArray,groupListType)
                editor.putString("messageList",strList)
            }
            editor.apply()
        }


//        var a = JSONArray()
//        for (i in 0..values.size - 1) {
//            a.put(values.get(i))
//        }
//        Log.d("TAG", "setStringasd: $a")
//        if(!values.isEmpty()){
//            prefs.edit().putString(key, a.toString())
//            Log.d("TAG", "setStringasd: ")
//        } else{
//            prefs.edit().putString(key, null)
//            Log.d("TAG", "setStringasd: $2222")
//        }
//        prefs.edit().commit()
    }

    // 삭제
    fun deleteString(key: String){
        prefs.edit().remove(key).apply()
    }
}