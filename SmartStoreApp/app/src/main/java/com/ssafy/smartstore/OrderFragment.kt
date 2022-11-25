package com.ssafy.smartstore

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.ssafy.smartstore.databinding.FragmentOrderBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.location.MyLocationActivity
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.*


class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private lateinit var storeRepository: StoreRepository
    private lateinit var productAdapter: OrderProductAdapter
    private lateinit var productTimeAdapter: OrderAdapter
    private lateinit var productAgeAdapter: OrderAdapter
    private lateinit var productGenderAdapter: OrderAdapter
    private lateinit var productProductAdapter: OrderAdapter

    private lateinit var ctx: Context
    private lateinit var activity: MainActivity
    private lateinit var userID: String
    private lateinit var userName: String
    private lateinit var userStamps: String

    private lateinit var customProgressDialog: com.ssafy.smartstore.ProgressDialog

    private var userOrder: MutableList<Order> = mutableListOf()

    private val UPDATE_INTERVAL = 1000 // 1초
    private val FASTEST_UPDATE_INTERVAL = 500 // 0.5초
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    //위치정보 요청시 호출
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                val distance = getDistance(
                    location.latitude,
                    location.longitude,
                    36.10830144233874,
                    128.41827450414362
                ).toString()
                binding.tvDistance.text = "매장과의 거리가 ${distance}m 입니다."
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        activity = ctx as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgressDialog = com.ssafy.smartstore.ProgressDialog(ctx)
        customProgressDialog.show()

        storeRepository = StoreRepository.get()

        userID = arguments?.getString("UserID").toString()
        userName = arguments?.getString("UserName").toString()
        userStamps = arguments?.getString("UserStamps").toString()

        CoroutineScope(Dispatchers.Main).launch {
            val orderDetailListTime: MutableList<Product> = mutableListOf()
            val orderDetailListAge: MutableList<Product> = mutableListOf()
            val orderDetailListGender: MutableList<Product> = mutableListOf()
            val orderDetailListProduct: MutableList<Product> = mutableListOf()

            launch {
                var userBirth = storeRepository.getUserAge(userID)
                Log.d("나이 : ", userBirth?.age.toString())
                var d: LocalDate = LocalDate.parse(
                    userBirth?.age,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
                val userAge = d.format(DateTimeFormatter.ofPattern("yyyy")).toInt()
                val userGender = storeRepository.getUserGender(userID)
                var minAge = userAge - 10
                var maxAge = userAge + 10
                Log.d("minAge : ", minAge.toString())
                Log.d("maxAge : ", maxAge.toString())
                val order: List<Order>? = storeRepository.getOrders()
                if (order != null) {
                    for (i in order) {
                        val h = LocalDateTime.parse(
                            i.orderTime,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        )
                        val current = h.format(DateTimeFormatter.ofPattern("HH"))
                        Log.d("시간대별 : ",current)

                        val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                            .format(DateTimeFormatter.ofPattern("HH"))
                        Log.d("현재시간 : ",now)

                        var orderDetail = storeRepository.getOrderDetails(i.id)
                        if (current == now) {
                            if (orderDetail != null) {
                                for (j in orderDetail) {
                                    var product = storeRepository.getProduct(j.productId)
                                    if (product != null) {
                                        orderDetailListTime.add(product)
                                    }
                                }
                            }
                        }

                        val age = i.userId?.let { storeRepository.getUserAge(it) }
                        d = LocalDate.parse(
                            age?.age,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        )

                        var ageOtherUser = d.format(DateTimeFormatter.ofPattern("yyyy")).toInt()
                        if (ageOtherUser in minAge until maxAge) {
                            if (orderDetail != null) {
                                for (j in orderDetail) {
                                    var product = storeRepository.getProduct(j.productId)
                                    if (product != null) {
                                        orderDetailListAge.add(product)
                                    }
                                }
                            }
                        }

                        val gender = i.userId?.let { storeRepository.getUserGender(it) }
                        if (userGender?.gender.toString() == gender?.gender.toString()) {
                            if (orderDetail != null) {
                                for (j in orderDetail) {
                                    var product = storeRepository.getProduct(j.productId)
                                    if (product != null) {
                                        orderDetailListGender.add(product)
                                    }
                                }
                            }
                        }

                        if (i.userId == userID) {
                            userOrder.add(i)
                        }
                    }
                }
                Log.d("userOrder : ", userOrder.size.toString())
                if (userOrder.size >= 1) {
                    var cntList: MutableMap<Int, Int> = mutableMapOf()
                    for (i in userOrder) {
                        var orderDetail = storeRepository.getOrderDetails(i.id)
                        if (orderDetail != null) {
                            for (j in orderDetail) {
                                if (!cntList.containsKey(j.productId)) {
                                    cntList[j.productId] = 1
                                } else {
                                    var tmp: Int? = cntList[j.productId]?.plus(1)
                                    if (tmp != null) {
                                        cntList[j.productId] = tmp
                                    }
                                }
                            }
                        }
                    }
                    if(cntList.isNotEmpty()){
                        cntList =
                            cntList.toList().sortedByDescending { it.second }.toMap() as MutableMap
                        val keys = cntList.keys.toMutableList()

                        var orderProductList: MutableList<Order> = mutableListOf()

                        if (order != null) {
                            for (i in order) {
                                var orderDetail = storeRepository.getOrderDetails(i.id)
                                if (orderDetail != null) {
                                    for (j in orderDetail) {
                                        if (j.productId == cntList[keys[0]]) {
                                            orderProductList.add(i)
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        var cntProductList: MutableMap<Int, Int> = mutableMapOf()
                        for (i in orderProductList) {
                            var orderDetail = storeRepository.getOrderDetails(i.id)
                            if (orderDetail != null) {
                                for (j in orderDetail) {
                                    if (j.productId == cntList[keys[0]]) {
                                        continue
                                    } else {
                                        if (!cntProductList.containsKey(j.productId)) {
                                            cntProductList[j.productId] = 1
                                        } else {
                                            var tmp: Int? = cntProductList[j.productId]?.plus(1)
                                            if (tmp != null) {
                                                cntProductList[j.productId] = tmp
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(cntProductList.isNotEmpty()){
                            cntProductList = cntProductList.toList().sortedByDescending { it.second }
                                .toMap() as MutableMap
                            val productKeys = cntProductList.keys.toMutableList()
                            for (i in productKeys) {
                                var product = storeRepository.getProduct(i)
                                if (product != null) {
                                    orderDetailListProduct.add(product)
                                }
                            }
                        }

                    }

                }
            }.join()
            delay(200L)
            launch {
                if (orderDetailListTime.size == 0) {
                    activity.runOnUiThread(Runnable {
                        binding.orderTime.visibility = View.GONE
                    })
                } else {
                    activity.runOnUiThread(Runnable {
                        binding.orderTime.visibility = View.VISIBLE
                    })
                    var lengthOrder: MutableList<Product> = if (orderDetailListTime.size <= 3) {
                        orderDetailListTime
                    } else {
                        orderDetailListTime.subList(0, 4)
                    }
                    productTimeAdapter = OrderAdapter(
                        ctx,
                        lengthOrder.distinct() as MutableList<Product>, userID
                    )
                    activity.runOnUiThread(Runnable {
                        binding.orderRecyclerviewTime.apply {
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = productTimeAdapter
                        }
                    })
                }

            }.join()
            delay(100L)
            launch {
                if (orderDetailListAge.size == 0) {
                    activity.runOnUiThread(Runnable {
                        binding.orderAge.visibility = View.GONE
                    })
                } else {
                    activity.runOnUiThread(Runnable {
                        binding.orderAge.visibility = View.VISIBLE
                    })
                    var lengthOrder: MutableList<Product> = if (orderDetailListAge.size <= 3) {
                        orderDetailListAge
                    } else {
                        orderDetailListAge.subList(0, 4)
                    }
                    productAgeAdapter = OrderAdapter(
                        ctx,
                        lengthOrder.distinct() as MutableList<Product>, userID
                    )
                    activity.runOnUiThread(Runnable {
                        binding.orderRecyclerviewAge.apply {
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = productAgeAdapter
                        }
                    })
                }

            }.join()
            delay(100L)
            launch {
                if (orderDetailListGender.size == 0) {
                    activity.runOnUiThread(Runnable {
                        binding.orderGender.visibility = View.GONE
                    })
                } else {
                    activity.runOnUiThread(Runnable {
                        binding.orderGender.visibility = View.VISIBLE
                    })
                    var lengthOrder: MutableList<Product> = if (orderDetailListGender.size <= 3) {
                        orderDetailListGender
                    } else {
                        orderDetailListGender.subList(0, 4)
                    }
                    productGenderAdapter = OrderAdapter(
                        ctx,
                        lengthOrder.distinct() as MutableList<Product>, userID
                    )
                    activity.runOnUiThread(Runnable {
                        binding.orderRecyclerviewGender.apply {
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = productGenderAdapter
                        }
                    })
                }

            }.join()
            delay(100L)
            launch {
                if (orderDetailListProduct.size == 0) {
                    activity.runOnUiThread(Runnable {
                        binding.orderProduct.visibility = View.GONE
                    })
                } else {
                    activity.runOnUiThread(Runnable {
                        binding.orderProduct.visibility = View.VISIBLE
                    })
                    var lengthOrder: MutableList<Product> = if (orderDetailListProduct.size <= 3) {
                        orderDetailListProduct
                    } else {
                        orderDetailListProduct.subList(0, 4)
                    }
                    productProductAdapter = OrderAdapter(
                        ctx,
                        lengthOrder.distinct() as MutableList<Product>, userID
                    )
                    activity.runOnUiThread(Runnable {
                        binding.orderRecyclerviewProduct.apply {
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = productProductAdapter
                        }
                    })
                }

            }.join()
            delay(100L)
            launch {
                val product = storeRepository.getProducts() as MutableList<Product>
                productAdapter = OrderProductAdapter(
                    ctx,
                    product, userID
                )
                activity.runOnUiThread(Runnable {
                    binding.productMenu.text = "상품(${product.size})"
                    binding.recyclerGridView.adapter = productAdapter
                    customProgressDialog.dismiss();
                })
            }
        }

        binding.spinner.apply {
            var spinnerAdapter = ArrayAdapter.createFromResource(
                ctx,
                com.ssafy.smartstore.R.array.itemList,
                com.ssafy.smartstore.R.layout.row_spinner
            )
            spinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            adapter = spinnerAdapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            Log.d("선택 내용 : ", "선택 안함")
                            CoroutineScope(Dispatchers.Main).launch {
                                productAdapter = OrderProductAdapter(
                                    ctx,
                                    storeRepository.getProducts() as MutableList<Product>, userID
                                )
                                activity.runOnUiThread(Runnable {
                                    binding.recyclerGridView.adapter = productAdapter
                                })
                            }
                        }
                        1 -> {
                            Log.d("선택 내용 : ", "조회순")
                            CoroutineScope(Dispatchers.Main).launch {
                                var products = storeRepository.getProducts() as MutableList<Product>
                                products.sortByDescending { it.views }
                                productAdapter = OrderProductAdapter(
                                    ctx,
                                    products, userID
                                )
                                activity.runOnUiThread(Runnable {
                                    binding.recyclerGridView.adapter = productAdapter
                                })
                            }
                        }
                        2 -> {
                            Log.d("선택 내용 : ", "판매순")
                            CoroutineScope(Dispatchers.Main).launch {
                                var products = storeRepository.getProducts() as MutableList<Product>
                                launch {
                                    val order: List<Order>? = storeRepository.getOrders()
                                    var cntList: MutableMap<Int, Int> = mutableMapOf()
                                    if (order != null) {
                                        for (i in order) {
                                            var orderDetail = storeRepository.getOrderDetails(i.id)
                                            if (orderDetail != null) {
                                                for (j in orderDetail) {
                                                    if (!cntList.containsKey(j.productId)) {
                                                        cntList[j.productId] = 1
                                                    } else {
                                                        var tmp: Int? =
                                                            cntList[j.productId]?.plus(1)
                                                        if (tmp != null) {
                                                            cntList[j.productId] = tmp
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(cntList.isNotEmpty()){
                                        cntList = cntList.toList().sortedByDescending { it.second }
                                            .toMap() as MutableMap
                                        var productsTmp: MutableList<Product> = mutableListOf()
                                        val keys = cntList.keys.toMutableList()
                                        if (cntList.size == products.size) {
                                            for (i in keys) {
                                                var product = storeRepository.getProduct(i)
                                                if (product != null) {
                                                    productsTmp.add(product)
                                                }
                                            }
                                            products = productsTmp
                                        } else {
                                            for (i in keys) {
                                                val it: MutableIterator<Product> = products.iterator()
                                                while (it.hasNext()) {
                                                    val item = it.next().id
                                                    if(item == i){
                                                        it.remove()
                                                    }
                                                }
                                            }
                                            for (i in keys) {
                                                var product = storeRepository.getProduct(i)
                                                if (product != null) {
                                                    productsTmp.add(product)
                                                }
                                            }
                                            productsTmp.addAll(products)
                                            products = productsTmp
                                        }
                                    }

                                }.join()
                                delay(100L)
                                launch {
                                    productAdapter = OrderProductAdapter(
                                        ctx,
                                        products, userID
                                    )
                                    activity.runOnUiThread(Runnable {
                                        binding.recyclerGridView.adapter = productAdapter
                                    })
                                }
                            }
                        }
                        3 -> {
                            Log.d("선택 내용 : ", "신상순")
                            CoroutineScope(Dispatchers.Main).launch {
                                var products = storeRepository.getProducts() as MutableList<Product>
                                products.sortByDescending { it.date }
                                productAdapter = OrderProductAdapter(
                                    ctx,
                                    products, userID
                                )
                                activity.runOnUiThread(Runnable {
                                    binding.recyclerGridView.adapter = productAdapter
                                })
                            }
                        }
                        else -> {
                            Log.d("선택 내용 : ", "평점순")
                            CoroutineScope(Dispatchers.Main).launch {
                                var products = storeRepository.getProducts() as MutableList<Product>
                                launch {
                                    var comments = storeRepository.getComments() as MutableList<Comment>
                                    var cntList: MutableMap<Int, Float> =
                                        mutableMapOf()
                                    var arr = mutableListOf<Int>()
                                    for(i in 0 until products.size){
                                        arr.add(0)
                                    }
                                    for (i in comments) {
                                        if (!cntList.containsKey(i.productId)) {
                                            cntList[i.productId] = i.rating
                                            arr[i.productId-1] = 1
                                        } else {
                                            var tmp = cntList[i.productId]
                                            if (tmp != null) {
                                                arr[i.productId-1] = arr[i.productId-1]+1
                                                cntList[i.productId] = (tmp+i.rating)/arr[i.productId-1]
                                            }
                                        }
                                    }
                                    cntList = cntList.toList().sortedByDescending { it.second }
                                        .toMap() as MutableMap
                                    var productsTmp: MutableList<Product> = mutableListOf()
                                    val keys = cntList.keys.toMutableList()
                                    if (cntList.size == products.size) {
                                        for (i in keys) {
                                            var product = storeRepository.getProduct(i)
                                            if (product != null) {
                                                productsTmp.add(product)
                                            }
                                        }
                                        products = productsTmp
                                    } else {
                                        Log.d("products : ", products.toString())
                                        for (i in keys) {
                                            val it: MutableIterator<Product> = products.iterator()
                                            while (it.hasNext()) {
                                                val item = it.next().id
                                                if(item == i){
                                                    it.remove()
                                                }
                                            }
                                        }
                                        for (i in keys) {
                                            var product = storeRepository.getProduct(i)
                                            if (product != null) {
                                                productsTmp.add(product)
                                            }
                                        }
                                        productsTmp.addAll(products)
                                        products = productsTmp
                                    }
                                }.join()
                                delay(100L)
                                launch {
                                    productAdapter = OrderProductAdapter(
                                        ctx,
                                        products, userID
                                    )
                                    activity.runOnUiThread(Runnable {
                                        binding.recyclerGridView.adapter = productAdapter
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }

        var productList = arguments?.getSerializable("list")
        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(ctx, ShoppingListActivity::class.java)
            intent.putExtra("list", productList)
            intent.putExtra("UserID", userID)
            intent.putExtra("UserStamps", userStamps)
            intent.putExtra("UserName", userName)
            ActivityCompat.startActivityForResult(ctx as Activity, intent, 200, null)
        }

        binding.btnLocation.setOnClickListener {
            var intent = Intent(context, MyLocationActivity::class.java)
            startActivity(intent)
        }

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL.toLong()
            smallestDisplacement = 10.0f
            fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )


    }

    private val R = 6372.8 * 1000
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(
            Math.toRadians(lat2)
        )
        val c = 2 * asin(sqrt(a))
        return (R * c).toInt()
    }


}