package com.ssafy.smartstore

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MyPageAdapter(
    private val context: Context,
    private val objects: List<Map<String, Object>>,
    private val activity: MainActivity,
    private val countList: MutableList<Int>
) :
    RecyclerView.Adapter<MyPageViewHolder>() {

    private var preOId: Int = -1
    private lateinit var storeRepository: StoreRepository

    // viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.items_my_page, parent, false)
        return MyPageViewHolder(itemView)
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {
        // 한 행에 데이터를 넣어준다.
        holder.apply {
            storeRepository = StoreRepository.get()
            CoroutineScope(Dispatchers.IO).launch {
                var oId = objects[position]["o_id"].toString().toDouble().toInt()
                Log.d("오더 아이디 : ", oId.toString())
                if (preOId != oId) {
                    activity.runOnUiThread(Runnable {
                        when (objects[position]["img"].toString()) {
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
                    activity.runOnUiThread(Runnable {
                        Log.d("countLsit : ", countList[position].toString())
                        if (countList[position] == 1) {
                            name.text = objects[position]["name"].toString()
                        } else {
                            name.text =
                                objects[position]["name"].toString() + " 외 " + (countList[position]-1).toString() + "잔"
                        }
                    })
                    CoroutineScope(Dispatchers.IO).launch {
                        var listViewItems: List<OrderDetail> =
                            storeRepository.getOrderDetails(oId)!!
                        var priceAll = 0
                        for (i in listViewItems) {
                            var product = storeRepository.getProduct(i.productId)
                            priceAll += (i.quantity* product?.price!!)
                        }
                        activity.runOnUiThread(Runnable { price.text = "Won. "+priceAll.toString() })
                        activity.runOnUiThread(Runnable {
                            var d: LocalDate = LocalDate.parse(
                                objects[position]["order_time"].toString(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'+'00:00")
                            )
                            var ageOtherUser = d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            date.text = ageOtherUser
                        })
                        activity.runOnUiThread(Runnable {
                            layout.setOnClickListener {
                                val intent = Intent(context, OrderDetailActivity::class.java)
                                intent.putExtra(
                                    "orderId",
                                    objects[position]["o_id"].toString()
                                )
                                intent.putExtra("priceAll", priceAll.toString())
                                intent.putExtra("date", objects[position]["order_time"].toString())
                                ActivityCompat.startActivity(context as Activity, intent, null)
                                context.overridePendingTransition(R.anim.slide_up_enter , R.anim.slide_up_exit)
                            }
                        })
                    }

                } else {
                    preOId = objects[position]["o_id"] as Int
                }

            }
        }
    }

    // 전체 아이템 개수 리턴
    override fun getItemCount(): Int {
        return objects.size
    }
}

// ViewHolder 클래스 생성
class MyPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name = itemView.findViewById(R.id.my_page_order_name) as TextView
    var price = itemView.findViewById(R.id.my_page_order_price) as TextView
    var date = itemView.findViewById(R.id.my_page_order_date) as TextView
    var sumnail = itemView.findViewById(R.id.my_page_order_img) as ImageView
    var layout = itemView.findViewById(R.id.my_page_order_layout) as LinearLayout
}