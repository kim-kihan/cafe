package com.ssafy.smartstore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.dto.Comment


class CommentAdapter(
    val context: Context,
    var list: List<Comment>,
    var userID: String
) : RecyclerView.Adapter<CommentViewAdapter>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    private lateinit var itemClickSaveListner: ItemClickListener
    private lateinit var itemClickModifyListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener, state: Int) {
        when (state) {
            0 -> this.itemClickListner = itemClickListener //delete
            1 -> this.itemClickModifyListner = itemClickListener //modify
            else -> this.itemClickSaveListner = itemClickListener //save
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewAdapter {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_comment, parent, false)

        return CommentViewAdapter(view)
    }

    override fun onBindViewHolder(holder: CommentViewAdapter, position: Int) {

        holder.apply {
            modify.visibility = View.GONE
            delete.visibility = View.GONE

            comment.text = list[position].comment
            if (userID == list[position].userId) {
                modify.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE
            }
            modify.setOnClickListener {
                itemClickModifyListner.onClick(it, position)
            }
            delete.setOnClickListener {
                itemClickListner.onClick(it, position)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

class CommentViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var comment = itemView.findViewById(R.id.comment) as TextView
    var modify = itemView.findViewById(R.id.comment_modify) as ImageButton
    var delete = itemView.findViewById(R.id.comment_delete) as ImageButton
}