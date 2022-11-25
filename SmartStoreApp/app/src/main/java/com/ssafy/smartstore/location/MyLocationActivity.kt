package com.ssafy.smartstore.location

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ssafy.smartstore.R
import java.io.IOException
import java.lang.Exception
import java.util.*

class MyLocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private val UPDATE_INTERVAL = 1000 // 1초
    private val FASTEST_UPDATE_INTERVAL = 500 // 0.5초

    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mCurrentLocation: Location
    private lateinit var currentPosition: LatLng

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_up_enter , R.anim.slide_up_exit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_location)

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL.toLong()
            smallestDisplacement = 10.0f
            fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap!!.setOnMarkerClickListener(this)

        //퍼미션 요청 대화상자 (권한이 없을때) & 실행 시 초기 위치를 서울 중심부로 이동
        setDefaultLocation()

        if (checkPermission()) { // 1. 위치 퍼미션을 가지고 있는지 확인
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식)
            startLocationUpdates() // 3. 위치 업데이트 시작
        } else {  //2. 권한이 없다면
            // 3-1. 사용자가 권한이 없는 경우에는
            val permissionListener = object : PermissionListener {
                // 권한 얻기에 성공했을 때 동작 처리
                override fun onPermissionGranted() {
                    startLocationUpdates()
                }
                // 권한 얻기에 실패했을 때 동작 처리
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@MyLocationActivity,
                        "위치 권한이 거부되었습니다.",
                        Toast.LENGTH_SHORT).show()
                }
            }

            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("[설정] 에서 위치 접근 권한을 부여해야만 사용이 가능합니다.")
                .setPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check()
        }
    }

    //위치정보 요청시 호출
    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                currentPosition = LatLng(location.latitude, location.longitude)
                val markerTitle: String = getCurrentAddress(currentPosition)
                val markerSnippet =
                    "위도: ${location.latitude}, 경도: ${location.longitude }"

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet)
                mCurrentLocation = location
            }
        }
    }

    private fun startLocationUpdates() {
        // 위치서비스 활성화 여부 check
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            if (checkPermission()) {
                mFusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()!!
                )
                if (mMap != null) mMap!!.isMyLocationEnabled = true
                //2. 지도에 +.- 버튼 보이기.
                if (mMap != null) mMap!!.uiSettings.isZoomControlsEnabled = true
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (checkPermission()) {
            //1. 중복코드제거
            startLocationUpdates()
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//            if (mMap != null && checkLocationServicesStatus()) mMap!!.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun getCurrentAddress(latlng: LatLng): String {
        //지오코더: GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }

        return if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(this, "주소 발견 불가", Toast.LENGTH_LONG).show()
            "주소 발견 불가"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    fun setCurrentLocation(location: Location, markerTitle: String?, markerSnippet: String?) {
        currentMarker?.remove()

        val currentLatLng = LatLng(36.10830144233874, 128.41827450414362)

        //4. 마커 바꾸기
        val bitmapdraw = ResourcesCompat.getDrawable(resources, R.drawable.location_icon, theme) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val marker = Bitmap.createScaledBitmap(b, 100, 100, false)

        val markerOptions = MarkerOptions()
        markerOptions.position(currentLatLng)
        markerOptions.title("싸피벅스")
        markerOptions.snippet("카페")
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(marker))
        markerOptions.draggable(true)

        currentMarker = mMap!!.addMarker(markerOptions)

        //3. animation으로 이동.
//        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
//        mMap!!.moveCamera(cameraUpdate)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
        mMap!!.animateCamera(cameraUpdate)
    }



    private fun setDefaultLocation() {
//        이전위치값 가져오기
        var lastLocation :Location
        if(checkPermission()) {
            mFusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
                if(location != null) lastLocation = location
            }
        }

        // 1. 중복코드제거
        //초기 위치를 서울로
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인 필요"

        var location = Location("")
        location.latitude = DEFAULT_LOCATION.latitude
        location.longitude = DEFAULT_LOCATION.longitude
        setCurrentLocation(location, markerTitle, markerSnippet)

//        currentMarker?.remove()
//
//        val markerOptions = MarkerOptions()
//        markerOptions.position(DEFAULT_LOCATION)
//        markerOptions.title(markerTitle)
//        markerOptions.snippet(markerSnippet)
//        markerOptions.draggable(true)
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//
//        currentMarker = mMap!!.addMarker(markerOptions)
//        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
//        mMap!!.moveCamera(cameraUpdate)
    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    /******** 위치서비스 활성화 여부 check *********/
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private var needRequest = false

    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS를 켰는지 검사함
                if (checkLocationServicesStatus()) {
                    needRequest = true
                    return
                }else{
                    settext("위치 서비스가 꺼져 있어, 현재 위치를 확인할 수 없습니다.")
                }
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val currentLatLng = LatLng(36.10830144233874, 128.41827450414362)
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder.setTitle("싸피벅스")
        builder.setView(R.layout.markerview)
        builder.setPositiveButton("전화걸기"){
                dialogInterface: DialogInterface, i: Int ->
            var intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:01012345678")
            if(intent.resolveActivity(packageManager) != null){
                startActivity(intent)
            }
        }

        builder.setNegativeButton("길찾기"){
                dialogInterface: DialogInterface, i: Int ->
            val gmmIntentUri = Uri.parse("google.navigation:q="+currentLatLng.latitude+","+currentLatLng.longitude + "&mode=t")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        builder.show()
        return true
    }


}