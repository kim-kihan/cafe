package com.ssafy.smartstore

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessagingService
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.dto.OrderProduct
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeAdapter(
    private val context: Context,
    private val objects: List<Map<String, Object>>,
    private val activity: MainActivity,
    private val countList: MutableList<Int>,
    private val UserName: String?,
    private val UserId: String,
    private val UserStamp: String
) : RecyclerView.Adapter<HomeViewHolder>() {

    private var preOId: Int = -1
    private lateinit var storeRepository: StoreRepository

    private lateinit var list: ArrayList<OrderProduct>

    // viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.items_home, parent, false)
        return HomeViewHolder(itemView)
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        // 한 행에 데이터를 넣어준다.
        holder.apply {
            storeRepository = StoreRepository.get()
            CoroutineScope(Dispatchers.IO).launch {
                var oId = objects[position]["o_id"].toString().toDouble().toInt()
                Log.d("TAG", "onBindViewHolder: $oId")
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
                            priceAll += (i.quantity * product?.price!!)
                        }
                        activity.runOnUiThread(Runnable { price.text = priceAll.toString() + "원" })
                        activity.runOnUiThread(Runnable {
                            var d: LocalDate = LocalDate.parse(
                                objects[position]["order_time"].toString(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'+'00:00")
                            )
                            var ageOtherUser = d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            date.text = ageOtherUser
                        })

                        activity.runOnUiThread(Runnable {
                            cartBtn.setOnClickListener {
                                CoroutineScope(Dispatchers.IO).launch {
                                    list = arrayListOf()
                                    var LastOrders: List<OrderDetail> =
                                        storeRepository.getOrderDetails(oId)!!
                                    //productid, price, count, name, img
                                    for (i in LastOrders) {
                                        var product = storeRepository.getProduct(i.productId)
                                        if (product != null) {
                                            list.add(
                                                OrderProduct(
                                                    i.productId.toString(),
                                                    product.name.toString(),
                                                    product.price.toString(),
                                                    i.quantity.toString(),
                                                    product.img.replace(".png", "")
                                                )
                                            )
                                        }
                                    }
                                    val intent = Intent(context, ShoppingListActivity::class.java)
                                    intent.putExtra("list", list)
                                    intent.putExtra("UserID", UserId)
                                    intent.putExtra("UserStamps", UserStamp)
                                    intent.putExtra("UserName", UserName)

                                    ActivityCompat.startActivity(context as Activity, intent, null)
                                    context.overridePendingTransition(R.anim.slide_up_enter , R.anim.slide_up_exit)
                                }

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
class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name = itemView.findViewById(R.id.home_order_name) as TextView
    var price = itemView.findViewById(R.id.home_order_price) as TextView
    var date = itemView.findViewById(R.id.home_order_date) as TextView
    var sumnail = itemView.findViewById(R.id.home_order_img) as ImageView
    var cartBtn = itemView.findViewById(R.id.home_cart_button) as ImageView

}