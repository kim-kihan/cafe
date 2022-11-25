package com.ssafy.smartstore

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderDetailAdapter(
    val context: Context,
    var list: List<OrderDetail>,
    val activity: OrderDetailActivity
) : RecyclerView.Adapter<OrderDetailHolderAdapter>() {

    private lateinit var storeRepository: StoreRepository

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailHolderAdapter {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_order_detail, parent, false)

        return OrderDetailHolderAdapter(view)
    }

    override fun onBindViewHolder(holder: OrderDetailHolderAdapter, position: Int) {

        holder.apply {
            storeRepository = StoreRepository.get()
            CoroutineScope(Dispatchers.IO).launch {
                val product = storeRepository.getProduct(list[position].productId)
                activity.runOnUiThread(Runnable {
                    name.text = product?.name
                })
                activity.runOnUiThread(Runnable {
                    price.text = product?.price.toString() + "원"
                })
                activity.runOnUiThread(Runnable {
                    quantity.text = list[position].quantity.toString() + "잔"
                    result.text = (product?.price!! * list[position].quantity.toString()
                        .toInt()).toString() + "원"
                })
                activity.runOnUiThread(Runnable {
                    when (product?.img) {
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
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

class OrderDetailHolderAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name = itemView.findViewById(R.id.order_detail_name) as TextView
    var price = itemView.findViewById(R.id.order_detail_price) as TextView
    var quantity = itemView.findViewById(R.id.order_detail_quantity) as TextView
    var result = itemView.findViewById(R.id.order_detail_price_all) as TextView
    var sumnail = itemView.findViewById(R.id.order_detail_sumnail) as ImageView
}