package com.ssafy.smartstore

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstore.databinding.ActivityShoppingListBinding
import com.ssafy.smartstore.dto.*
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// F06: 주문 관리 - 상품 주문 - 로그인한 사용자는 상품 상세 화면 에서 n개를 선정하여 장바구니에 등록할 수 있다. 로그인 한 사용자만 자기의 계정으로 구매를 처리할 수 있다.
// 장바구니 화면

private const val TAG = "ShoppingListActivity_싸피"

class ShoppingListActivity : AppCompatActivity() {
    // NFC
    var nfcAdapter: NfcAdapter? = null
    var pIntent: PendingIntent? = null
    private var filters: Array<IntentFilter>? = null
    private var dialog: AlertDialog? = null
    private var table: String = ""

    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var orderProduct: ArrayList<OrderProduct>
    private lateinit var productAdapter: ShoppingAdapter
    private lateinit var storeRepository: StoreRepository

    private var resultPrice = 0
    private var resultCount = 0

    private lateinit var user: String

    private lateinit var userName: String
    private var userStamps: Int = 0

    private var OrderLocation: Boolean = true



    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    fun settext(text: String){
        var toastLayout = layoutInflater.inflate(R.layout.toast_layout, null)

        var textView: TextView? = toastLayout.findViewById(R.id.toast)
        textView?.text = text
        toastLayout.setBackgroundResource(R.drawable.toast_back)

        var toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        toast.view = toastLayout
        toast.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if(nfcAdapter == null){
            finish()
        }

        //SingleTop 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // 태그 타입이면 해당 인텐트 수신 가능
        val tag_filter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        filters = arrayOf(tag_filter)

        if (intent.getSerializableExtra("list") != null) {
            orderProduct = intent.getSerializableExtra("list") as ArrayList<OrderProduct>
            productAdapter = ShoppingAdapter(this, orderProduct)

            productAdapter.setItemClickListener(object : ShoppingAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int) {
                    orderProduct.removeAt(position)
                    updateListView(position)
                }
            })

            productAdapter.setItemClickListener1(object : ShoppingAdapter.ItemClickPriceListener {
                override fun onClick(view: View, position: Int, price: String, minus: String) {
                    Log.d("asd", "resultPrice: ${resultPrice}")
                    resultPrice = resultPrice - minus.toInt() + price.toInt()
                    binding.shoppingOrderButton.text = "Order Now                                              Won. "+resultPrice.toString()
                    updateListView(position)
                }
            })

            binding.shoppingRecyclerview.apply {
                adapter = productAdapter
            }

            for (i in orderProduct) {
                resultPrice += (i.price.toInt() * i.count.toInt())
                resultCount += i.count.toInt()
            }
        }


        storeRepository = StoreRepository.get()

        binding.shoppingOrderButton.text = "Order Now                                              Won. "+resultPrice.toString()
        binding.shoppingResultCount.text = "Total " + resultCount.toString() + " cup"

        user = intent.getStringExtra("UserID").toString()
        userStamps = intent.getStringExtra("UserStamps").toString().toInt()
        userName = intent.getStringExtra("UserName").toString()

        binding.toggle.visibility = View.VISIBLE
        binding.toggle2.visibility = View.GONE

        binding.takeOut.setOnClickListener {
            binding.toggle.visibility = View.GONE
            binding.toggle2.visibility = View.VISIBLE
            OrderLocation = false
        }

        binding.store2.setOnClickListener {
            binding.toggle.visibility = View.VISIBLE
            binding.toggle2.visibility = View.GONE
            OrderLocation = true
        }

        binding.shoppingOrderButton.setOnClickListener {
            if(!OrderLocation){
                CoroutineScope(Dispatchers.IO).launch {
                    launch {
                        storeRepository.insertOrder(Order(user, "", "", "N"))
                    }.join()
                    launch {
                        var getOrderList = storeRepository.getOrders()
                        var getOrder = getOrderList?.get(0)
                        for (i in orderProduct) {
                            if (getOrder != null) {
                                storeRepository.insertOrderDetail(
                                    OrderDetail(
                                        getOrder.id+1,
                                        i.productId.toInt(),
                                        i.count.toInt()
                                    )
                                )

                            }
                        }
                    }.join()
                    delay(100L)
                    launch {
                        val getOrder: List<Order>? = storeRepository.getOrders()
                        delay(100L)
                        var getDetail = getOrder?.get(0)?.id?.let { it1 ->
                            storeRepository.getOrderDetails(
                                it1
                            )
                        }
                        var cnt = 0
                        if (getDetail != null) {
                            for(i in getDetail){
                                cnt += i.quantity
                            }
                            getOrder?.get(0)?.id?.let { it1 ->
                                storeRepository.insertStamp(
                                    Stamp(
                                        user,
                                        it1, cnt
                                    )
                                )
                            }
                            userStamps = cnt
                        }

                    }.join()
                    delay(100L)
                    launch {
                        storeRepository.updateUser(User(user,userName,"",userStamps))
                    }
                }
                // 주문 완료 시, 인텐트 1번
                settext("주문이 완료되었습니다.")
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else{
                if(resultPrice == 0){
                    settext("주문 목록을 추가해주세요.")
                } else{
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                    builder.setTitle("알림")
                    builder.setMessage("Table NFC를 찍어주세요.\n")
                    builder.setCancelable(true)
                    builder.setNegativeButton("취소") { dialog, _ ->
                        nfcAdapter!!.disableForegroundDispatch(this)
                        dialog.cancel()
                    }
                    dialog = builder.create()
                    dialog!!.show()
                    nfcAdapter!!.enableForegroundDispatch(this, pIntent, filters, null)
                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //다이얼로그가 띄워져 있는 상태(showing)인 경우 dismiss() 호출
        if (dialog != null && dialog!!.isShowing) { dialog!!.dismiss() }

    }
    fun processIntent(intent:Intent){
        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        Log.d(TAG, "onNewIntent: rawmgs$rawMsgs")
        if (rawMsgs != null) {
            val message = arrayOfNulls<NdefMessage>(rawMsgs.size)
            for (i in rawMsgs.indices) {
                message[i] = rawMsgs[i] as NdefMessage
            }
            val record_data = message[0]!!.records[0]
            val record_type = record_data.type
            val type = String(record_type)
            // 가져온 데이터를 TextView에 반영
            val data = message[0]!!.records[0].payload
            Log.d(TAG, "onNewIntent: mesg$message")
            Log.d(TAG, "onNewIntent: data$data")
            table = "${String(data, 3, data.size - 3)}"
        }

        CoroutineScope(Dispatchers.IO).launch {
            launch {
                storeRepository.insertOrder(Order(user, "", "", "N"))
            }.join()
            launch {
                var getOrderList = storeRepository.getOrders()
                var getOrder = getOrderList?.get(0)
                for (i in orderProduct) {
                    if (getOrder != null) {
                        storeRepository.insertOrderDetail(
                            OrderDetail(
                                getOrder.id+1,
                                i.productId.toInt(),
                                i.count.toInt()
                            )
                        )

                    }
                }
            }.join()
            delay(100L)
            launch {
                val getOrder: List<Order>? = storeRepository.getOrders()
                delay(100L)
                var getDetail = getOrder?.get(0)?.id?.let { it1 ->
                    storeRepository.getOrderDetails(
                        it1
                    )
                }
                var cnt = 0
                if (getDetail != null) {
                    for(i in getDetail){
                        cnt += i.quantity
                    }
                    getOrder?.get(0)?.id?.let { it1 ->
                        storeRepository.insertStamp(
                            Stamp(
                                user,
                                it1, cnt
                            )
                        )
                    }
                    userStamps = cnt
                }

            }.join()
            delay(100L)
            launch {
                storeRepository.updateUser(User(user,userName,"",userStamps))
            }
        }
        // 주문 완료 시, 인텐트 1번
        settext("주문이 완료되었습니다.")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED) || intent.action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            processIntent(intent)
        }
    }

    private fun updateListView(position: Int) {
        productAdapter = ShoppingAdapter(this, orderProduct)

        productAdapter.setItemClickListener(object : ShoppingAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                orderProduct.removeAt(position)
                updateListView(position)
            }
        })

        productAdapter.setItemClickListener1(object : ShoppingAdapter.ItemClickPriceListener {
            override fun onClick(view: View, position: Int, price: String, minus: String) {
                Log.d("asd", "resultPrice: ${resultPrice}")
                resultPrice = resultPrice - minus.toInt() + price.toInt()
                binding.shoppingOrderButton.text = "Order Now                                              Won. "+resultPrice.toString()
                updateListView(position)
            }
        })

        binding.shoppingRecyclerview.apply {
            adapter = productAdapter
        }

    }

    override fun onBackPressed() {
        // 뒤로가기 시 인텐트 2번
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }
}