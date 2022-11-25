package com.ssafy.smartstore

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.ssafy.smartstore.database.OrderDao
import com.ssafy.smartstore.databinding.ActivityMainBinding
import com.ssafy.smartstore.dto.OrderProduct
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.firebase.FirebaseTokenService
import org.altbeacon.beacon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

data class Link<T> (var button: Button, var target: Class<T>)

private const val TAG = "MainActivity_싸피"
class MainActivity : AppCompatActivity(), BeaconConsumer{

    // Beacon
    private lateinit var beaconManager: BeaconManager
    private val BEACON_UUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825"
    private val BEACON_MAJOR = "10004"
    private val BEACON_MINOR = "54480"

    // Beacon의 Region 설정
    private val region = Region("estimote"
        , Identifier.parse(BEACON_UUID)
        , Identifier.parse(BEACON_MAJOR)
        , Identifier.parse(BEACON_MINOR)
    )

    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var needBLERequest = true

    private val STORE_DISTANCE = 1
    private var STORE_INFO_POPUP = false

    private val PERMISSIONS_CODE = 100

    // 모든 퍼미션 관련 배열
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    companion object {
        // Notification Channel ID
        const val channel_id = "ssafy_store_channel"

        fun uploadToken(token: String) {
            // 새로운 토큰 수신 시 서버로 전송
            val storeService = StoreApplicationClass.retrofit.create(FirebaseTokenService::class.java)
            storeService.uploadToken(token).enqueue(object : Callback<String> {

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        Log.d(TAG, "onResponse: $res")
                    }
                    else {
                        Log.d(TAG, "onResponse: Error Code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, t.message ?: "토큰 정보 등록 중 통신오류")
                }
            })
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var list: ArrayList<OrderProduct>
    private lateinit var homeFragment: HomeFragment
    private lateinit var orderFragment: OrderFragment
    private lateinit var myPageFragment: MyPageFragment
    private lateinit var searchFragment: SearchFragment

    val bundle = Bundle()

    private val onNavigationItemSelectedListener = BottomNavigationView
        .OnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.home_fragment -> {
                    homeFragment = HomeFragment()
                    val data = intent.getSerializableExtra("User") as User
                    bundle.putString("UserName", data.name)
                    bundle.putString("UserID", data.id)
                    bundle.putString("UserStamps", data.stamps.toString())
                    homeFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host, homeFragment).commit()
                }
                R.id.order_fragment -> {
                    orderFragment = OrderFragment()
                    val data = intent.getSerializableExtra("User") as User
                    bundle.putString("UserName", data.name)
                    bundle.putString("UserID", data.id)
                    bundle.putString("UserStamps", data.stamps.toString())
                    Log.d("User", data.stamps.toString())
                    orderFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host, orderFragment).commit()
                }
                R.id.page_fragment -> {
                    myPageFragment = MyPageFragment()
                    val data = intent.getSerializableExtra("User") as User
                    Log.d("User", data.stamps.toString())
                    bundle.putString("UserName", data.name)
                    bundle.putString("UserID", data.id)
                    bundle.putString("UserStamps", data.stamps.toString())
                    myPageFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host, myPageFragment).commit()
                }
                R.id.search_fragment -> {
                    searchFragment = SearchFragment()
                    val data = intent.getSerializableExtra("User") as User
                    Log.d("User", data.stamps.toString())
                    bundle.putString("UserName", data.name)
                    bundle.putString("UserID", data.id)
                    bundle.putString("UserStamps", data.stamps.toString())
                    searchFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host, searchFragment).commit()
                }
            }
            true
        }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }

        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))

        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = arrayListOf()

        val data = intent.getSerializableExtra("User") as User

        var homeFragments = HomeFragment()
        bundle.putString("UserName", data.name)
        bundle.putString("UserID", data.id)
        bundle.putString("UserStamps", data.stamps.toString())



        homeFragments.arguments = bundle

        supportFragmentManager.beginTransaction()
            .add(R.id.nav_host, homeFragments).commit()

        binding.bottomNavigationView
            .setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }

            // 새로운 FCM 등록 토큰을 얻음
            uploadToken(task.result!!)

            // token log 남기기
            Log.d(TAG, "token: ${task.result ?: "task.result is null"}")
        })

        createNotificationChannel(channel_id, "ssafy")

        startScan()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    // Notification 수신을 위한 채널 추가
    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "createNotificationChannel: $notificationManager")
        notificationManager.createNotificationChannel(channel)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            Log.d("MDM", "In onActivityResult")
            when(requestCode){
                100 -> {
                    val id = data?.getStringExtra("productId")
                    val price = data?.getStringExtra("price")
                    val count = data?.getStringExtra("count")
                    val name = data?.getStringExtra("name")
                    val img = data?.getStringExtra("img")

                    val bundle = Bundle()

                    if(id!=null&&price!=null&&name!=null&&count!=null&&img!=null){
                        list.add(OrderProduct(id,name,price,count,img))
                        bundle.putSerializable("list",list)
                    }
                    val data = intent.getSerializableExtra("User") as User
                    bundle.putString("UserName", data.name)
                    bundle.putString("UserID", data.id)
                    bundle.putString("UserStamps", data.stamps.toString())
                    val fragment = OrderFragment()
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host, fragment).commit()

                }
                200 -> {
                    list = arrayListOf()
                    val bundle = Bundle()
                    val fragment = OrderFragment()
                    fragment.arguments = bundle
                    val data = intent.getSerializableExtra("User") as User
                    bundle.putString("UserName", data.name)
                    bundle.putString("UserID", data.id)
                    bundle.putString("UserStamps", data.stamps.toString())
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host, fragment).commit()

                }
                300 -> {
                    val bundle = Bundle()
                    val fragment = SearchFragment()
                    fragment.arguments = bundle
                    val data = intent.getSerializableExtra("User") as User
                    bundle.putString("UserName", data.name)
                    bundle.putString("UserID", data.id)
                    bundle.putString("UserStamps", data.stamps.toString())
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host, fragment).commit()

                }
            }
        }
    }

    // 블루투스 켰는지 확인
    private fun isEnableBLEService(): Boolean{
        if(!bluetoothAdapter!!.isEnabled){
            return false
        }
        return true
    }

    // Beacon Scan 시작
    private fun startScan() {
        // 블루투스 Enable 확인
        if(!isEnableBLEService()){
            requestEnableBLE()
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        }

        // 위치 정보 권한 허용 및 GPS Enable 여부 확인
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }
        Log.d(TAG, "startScan: beacon Scan start")

        // Beacon Service bind
        beaconManager.bind(this)
    }

    // 블루투스 ON/OFF 여부 확인 및 키도록 하는 함수
    private fun requestEnableBLE(){
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        // 사용자의 블루투스 사용이 가능한지 확인
        if (isEnableBLEService()) {
            needBLERequest = false
            startScan()
        }
    }

    // 위치 정보 권한 요청 결과 콜백 함수
    // ActivityCompat.requestPermissions 실행 이후 실행
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i(TAG, "$permission 권한 획득에 실패하였습니다.")
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 발견하였습니다.")
                    beaconManager.startRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didExitRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 찾을 수 없습니다.")
                    beaconManager.stopRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didDetermineStateForRegion(i: Int, region: Region?) {}
        })

        beaconManager.addRangeNotifier { beacons, region ->
            for (beacon in beacons) {
                // Major, Minor로 Beacon 구별
                if(isYourBeacon(beacon)){
                    // 한번만 띄우기 위한 조건
                    if(!STORE_INFO_POPUP){
                        showStoreEventDialog()
                    }
                    //storeDistanceTV.text = "가맹점과의 거리 : ${String.format("%.2f",beacon.distance)}m"
                }
                Log.d(TAG, "distance: " + beacon.distance + " id:" + beacon.id1 + "/" + beacon.id2 + "/" + beacon.id3)
            }

            if(beacons.isEmpty()){
                //storeDistanceTV.text = "가맹점을 찾을 수 없습니다."
            }
        }

        try {
            beaconManager.startMonitoringBeaconsInRegion(region)
        } catch (e: RemoteException){
            e.printStackTrace()
        }
    }

    // 찾고자 하는 Beacon이 맞는지, 정해둔 거리 내부인지 확인
    private fun isYourBeacon(beacon: Beacon): Boolean {
        return (beacon.id2.toString() == BEACON_MAJOR &&
                beacon.id3.toString() == BEACON_MINOR &&
                beacon.distance <= STORE_DISTANCE
                )
    }

    private fun showStoreEventDialog(){
        STORE_INFO_POPUP = true
        val data = intent.getSerializableExtra("User") as User
        var order_service = StoreApplicationClass.retrofit.create(OrderDao::class.java)
        order_service.getLastMonthOrder(data.id).enqueue(object : Callback<List<Map<String, Object>>> {
            override fun onResponse(
                call: Call<List<Map<String, Object>>>,
                response: Response<List<Map<String, Object>>>
            ) {
                val res = response.body()
                if(response.code() == 200){
                    val storeEventDialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_store_info, null)
                    if(res != null){
                        // dialog
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity, R.style.MyDialogTheme)
                        builder.apply {
                            setView(storeEventDialogView)
                            setTitle("")
                            setCancelable(true)
                            setPositiveButton("확인") { dialog, _ ->
                                dialog.cancel()
                            }
                        }
                        builder.create().show()
                    }
                }
                else{
                    Toast.makeText(this@MainActivity, "상가 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "상가 정보 불러오는 중 통신오류")
                }
            }

            override fun onFailure(call: Call<List<Map<String, Object>>>, t: Throwable) {
                Log.d(TAG, t.message ?: "상가 정보 불러오는 중 통신오류")
            }
        })
    }

    // LAT, LNG -> 주소로 변환
    private fun getCurrentAddress(latLng: LatLng): String {
        //지오코더: GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Log.d(TAG, "getCurrentAddress: 지오코더 서비스 사용불가")
            return "지오코더 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.d(TAG, "getCurrentAddress: 잘못된 GPS 좌표")
            return "잘못된 GPS 좌표"
        }
        return if (addresses == null || addresses.isEmpty()) {
            Log.d(TAG, "getCurrentAddress: 주소 발견 불가")
            "주소 발견 불가"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    // 꼭 Destroy를 시켜서 beacon scan을 중지 시켜야 한다.
    // beacon scan을 중지 하지 않으면 일정 시간 이후 다시 scan이 가능하다.
    override fun onDestroy() {
        beaconManager.stopMonitoringBeaconsInRegion(region)
        beaconManager.stopRangingBeaconsInRegion(region)
        beaconManager.unbind(this)
        super.onDestroy()
    }
}