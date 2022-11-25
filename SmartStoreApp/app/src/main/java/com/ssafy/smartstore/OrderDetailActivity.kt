package com.ssafy.smartstore

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.ssafy.smartstore.databinding.ActivityOrderDetailBinding
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// F08: 주문관리 - 주문 상세 조회 - 주문 번호에 기반하여 주문을 조회할 수 있다. 이때 주문 상세 항목들 어떤 상품이 몇개 주문 되었는지에 대한 정보도 포함한다.

private const val TAG = "OrderDetailActivity_싸피"
class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var storeRepository: StoreRepository
    private lateinit var orderDetailList: List<OrderDetail>
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private lateinit var orderId:String
    private lateinit var priceAll:String
    private lateinit var date:String

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getStringExtra("orderId").toString()
        priceAll = intent.getStringExtra("priceAll").toString()
        date = intent.getStringExtra("date").toString()

        storeRepository = StoreRepository.get()

        CoroutineScope(Dispatchers.IO).launch{
            orderDetailList = storeRepository.getOrderDetails(orderId.toDouble().toInt())!!
            orderDetailAdapter = OrderDetailAdapter(context,orderDetailList,this@OrderDetailActivity)
            this@OrderDetailActivity.runOnUiThread(Runnable {
                binding.recyclerViewOrderDetail.apply {
                    adapter = orderDetailAdapter
                }
            })


        }
        var d: LocalDate = LocalDate.parse(
            date,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'+'00:00")
        )
        var ageOtherUser = d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        binding.dateOrderDetail.text = ageOtherUser
        binding.priceOrderDetail.text = priceAll
    }
}