package com.ssafy.smartstore

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.ssafy.smartstore.databinding.ActivityLoginBinding
import com.ssafy.smartstore.databinding.ActivitySearchResultBinding
import com.ssafy.smartstore.dto.OrderProduct
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding

    private lateinit var content: String
    private lateinit var userID: String
    private lateinit var userName: String
    private lateinit var userStamps: String
    private var productList: ArrayList<OrderProduct> = arrayListOf()

    private lateinit var storeRepository: StoreRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        content = intent.getStringExtra("content").toString()
        userID = intent.getStringExtra("UserID").toString()
        userName = intent.getStringExtra("UserName").toString()
        userStamps = intent.getStringExtra("UserStamps").toString()


        binding.textView4.visibility = View.GONE

        storeRepository = StoreRepository.get()

        binding.backImg.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
            finish()
        }

        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this@SearchResultActivity, ShoppingListActivity::class.java)
            intent.putExtra("list", productList)
            intent.putExtra("UserID", userID)
            intent.putExtra("UserStamps", userStamps)
            intent.putExtra("UserName", userName)
            ActivityCompat.startActivityForResult(this@SearchResultActivity, intent, 200, null)
        }

        CoroutineScope(Dispatchers.Main).launch {
            var products = storeRepository.getProducts()
            var contents: MutableList<Product> = mutableListOf()
            if (products != null) {
                for (i in products) {
                    if (i.name?.contains(content) == true) {
                        contents.add(i)
                    }
                }
                if (contents.size > 0) {
                    this@SearchResultActivity.runOnUiThread(Runnable {
                        binding.recyclerGridView.adapter =
                            OrderProductAdapter(
                                this@SearchResultActivity,
                                contents, userID
                            )
                    })
                } else {
                    binding.textView4.visibility = View.VISIBLE
                    binding.recyclerGridView.visibility = View.GONE
                }
            }
        }
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

                    if(id!=null&&price!=null&&name!=null&&count!=null&&img!=null){
                        productList.add(OrderProduct(id,name,price,count,img))
                    }
                }
            }
        }
    }

}