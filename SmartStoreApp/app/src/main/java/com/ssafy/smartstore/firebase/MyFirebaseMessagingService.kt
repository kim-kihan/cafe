package com.ssafy.smartstore.firebase

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstore.LoginActivity
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.dto.MessageDTO
import java.lang.reflect.Type


private const val TAG = "MyFMS_싸피"
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 새로운 토큰이 생성될 때 마다 해당 콜백이 호출된다.
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // 새로운 토큰 수신 시 서버로 전송
        MainActivity.uploadToken(token)

        // 새로운 토큰 로그 남기기
        Log.d(TAG, "onNewToken: $token")
    }

    // Foreground에서 Push Service를 받기위해 Notification 설정
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification.let { message ->
            val messageTitle = message!!.title
            val messageContent = message!!.body

            val sdf = getSharedPreferences("notice", MODE_PRIVATE)
            val editor = sdf.edit()
            val gson = GsonBuilder().create()
            val data = MessageDTO(messageTitle, messageContent)
            val tempArray = ArrayList<MessageDTO>()
            val groupListType: Type = object : TypeToken<ArrayList<MessageDTO?>?>(){}.type

            val prev = sdf.getString("messageList", "none")
            val convertedData = gson.toJson(prev)

            if(prev!="none"){ //데이터가 비어있지 않다면?
                if(prev!="[]" || prev!="")tempArray.addAll(gson.fromJson(prev,groupListType))
                tempArray.add(data)
                val strList = gson.toJson(tempArray,groupListType)
                editor.putString("messageList",strList)
            }else{
                tempArray.add(data)
                val strList = gson.toJson(tempArray,groupListType)
                editor.putString("messageList",strList)
            }
            editor.apply()

            val Intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val PendingIntent =
                PendingIntent.getActivity(this, 0, Intent, 0)

            val builder = NotificationCompat.Builder(this, MainActivity.channel_id)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(messageTitle)
                .setContentText(messageContent)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent)

            NotificationManagerCompat.from(this).apply {
                notify(101, builder.build())
            }
        }
    }
}