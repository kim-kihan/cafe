package com.ssafy.smartstore.firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.MainActivity
import com.ssafy.smartstore.R
import android.content.Context
import android.util.Log
import com.ssafy.smartstore.StoreApplicationClass
import com.ssafy.smartstore.dto.MessageDTO

class NoticeAdapter(var objects: ArrayList<MessageDTO>)
    :RecyclerView.Adapter<NoticeHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    //클릭리스너 선언
    private lateinit var noticeClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(noticeClickListener: ItemClickListener) {
        this.noticeClickListner = noticeClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_notice, parent, false)

        return NoticeHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
        holder.apply {
            notice.text = objects[position].content

            cancelBtn.setOnClickListener{
                noticeClickListner.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return objects.size
    }

}

class NoticeHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView) {
    var notice = itemView.findViewById(R.id.textNoticeContent) as TextView
    var cancelBtn = itemView.findViewById(R.id.notice_btn) as ImageView

}