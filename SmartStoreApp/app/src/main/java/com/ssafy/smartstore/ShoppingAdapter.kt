package com.ssafy.smartstore

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.dto.OrderProduct
import com.ssafy.smartstore.dto.Product

class ShoppingAdapter(val context: Context, var list: ArrayList<OrderProduct>): RecyclerView.Adapter<ShopAdapter>() {

    var minusPrice: String = ""

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }
    interface ItemClickPriceListener {
        fun onClick(view: View, position: Int, price: String, minus: String)
    }

    //클릭리스너 선언
    private lateinit var itemClickPriceListner: ItemClickPriceListener
    private lateinit var itemClickCancelListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickCancelListner = itemClickListener //modify
    }
    fun setItemClickListener1(itemClickListener: ItemClickPriceListener) {
        this.itemClickPriceListner = itemClickListener //save
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopAdapter {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.items_shopping_list, parent, false)

        return ShopAdapter(view)
    }

    override fun onBindViewHolder(holder: ShopAdapter, position: Int) {

        holder.apply {
            name.text = list[position].name
            price.text = "price "+list[position].price
            count.text = list[position].count+" cup"
            result.text = "Won. "+(list[position].price.toInt()*list[position].count.toInt()).toString()

            cancel.setOnClickListener {
                Log.d("포지션 값은? : ", position.toString())
                itemClickCancelListner.onClick(it, position)
            }
            add.setOnClickListener {
                minusPrice = (list[position].price.toInt()*list[position].count.toInt()).toString()
                count.text = (list[position].count.toInt()+1).toString()+" cup"
                list[position].count = (list[position].count.toInt()+1).toString()
                result.text = "Won. "+(list[position].price.toInt()*(list[position].count.toInt())).toString()
                itemClickPriceListner.onClick(it, position,(list[position].price.toInt()*(list[position].count.toInt())).toString(), minusPrice)

            }
            minus.setOnClickListener {
                minusPrice = (list[position].price.toInt()*list[position].count.toInt()).toString()
                count.text = (list[position].count.toInt()-1).toString()+" cup"
                list[position].count = (list[position].count.toInt()-1).toString()
                result.text = "Won. "+(list[position].price.toInt()*(list[position].count.toInt())).toString()
                itemClickPriceListner.onClick(it, position,(list[position].price.toInt()*(list[position].count.toInt())).toString(), minusPrice)
            }

            when(list[position].img){
                "coffee1" -> sumnail.setImageResource(R.drawable.coffee1)
                "coffee2" -> sumnail.setImageResource(R.drawable.coffee2)
                "coffee3" -> sumnail.setImageResource(R.drawable.coffee3)
                "coffee4" -> sumnail.setImageResource(R.drawable.coffee4)
                "coffee5" -> sumnail.setImageResource(R.drawable.coffee5)
                "coffee6" -> sumnail.setImageResource(R.drawable.coffee6)
                "coffee7" -> sumnail.setImageResource(R.drawable.coffee7)
                "coffee8" -> sumnail.setImageResource(R.drawable.coffee8)
                "coffee9" -> sumnail.setImageResource(R.drawable.coffee9)
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

class ShopAdapter(itemView: View)
    : RecyclerView.ViewHolder(itemView) {
    var name = itemView.findViewById(R.id.shopping_name) as TextView
    var price = itemView.findViewById(R.id.shopping_price) as TextView
    var count = itemView.findViewById(R.id.shopping_count) as TextView
    var result = itemView.findViewById(R.id.shopping_count_price) as TextView
    var sumnail = itemView.findViewById(R.id.shopping_img) as ImageView
    var cancel = itemView.findViewById(R.id.shopping_cancel) as FrameLayout
    var add = itemView.findViewById(R.id.menu_detail_plus) as ImageButton
    var minus = itemView.findViewById(R.id.menu_detail_minus) as ImageButton
}