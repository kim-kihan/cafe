package com.ssafy.smartstore

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderProductAdapter(val context: Context, var list: MutableList<Product>, var userID: String): RecyclerView.Adapter<ListAdapter>() {

    private lateinit var storeRepository: StoreRepository

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_frag_product, parent, false)

        return ListAdapter(view)
    }

    override fun onBindViewHolder(holder: ListAdapter, position: Int) {

        holder.apply {
            name.text = list[position].name
            type.text = list[position].type
            price.text = "Won. "+list[position].price.toString()
            layout.setOnClickListener {
                storeRepository = StoreRepository.get()
                CoroutineScope(Dispatchers.Main).launch {
                    var product = storeRepository.getProduct(list[position].id)
                    if(product!=null){
                        product.views = product.views + 1
                        val a = storeRepository.updateProduct(product)
                        Log.d("product 업데이트 : ", a.toString())
                    }
                }
                val intent = Intent(context, MenuDetailActivity::class.java)
                intent.putExtra("productId",list[position].id.toString())
                intent.putExtra("UserID",userID)
                ActivityCompat.startActivityForResult(context as Activity, intent, 100, null)

                context.overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
            }
            when(list[position].img){
                "coffee1.png" -> sumnail.setImageResource(R.drawable.coffee1)
                "coffee2.png" -> sumnail.setImageResource(R.drawable.coffee2)
                "coffee3.png" -> sumnail.setImageResource(R.drawable.coffee3)
                "coffee4.png" -> sumnail.setImageResource(R.drawable.coffee4)
                "coffee5.png" -> sumnail.setImageResource(R.drawable.coffee5)
                "coffee6.png" -> sumnail.setImageResource(R.drawable.coffee6)
                "coffee7.png" -> sumnail.setImageResource(R.drawable.coffee7)
                "coffee8.png" -> sumnail.setImageResource(R.drawable.coffee8)
                "coffee9.png" -> sumnail.setImageResource(R.drawable.coffee9)
                else -> sumnail.setImageResource(R.drawable.cookie)
            }
            val drawable = context.getDrawable(R.drawable.img_item) as GradientDrawable?
            sumnail.background = drawable
            sumnail.clipToOutline = true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

class ListAdapter(itemView: View)
    : RecyclerView.ViewHolder(itemView) {
    var name = itemView.findViewById(R.id.textListTitle) as TextView
    var price = itemView.findViewById(R.id.textListPrice) as TextView
    var type = itemView.findViewById(R.id.textListCategory) as TextView
    var layout = itemView.findViewById(R.id.layoutListItem) as LinearLayout
    var sumnail = itemView.findViewById(R.id.img_product) as ImageView

}