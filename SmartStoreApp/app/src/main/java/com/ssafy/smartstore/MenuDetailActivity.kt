package com.ssafy.smartstore

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.ssafy.smartstore.databinding.ActivityMenuDetailBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.*
import java.lang.Runnable


// F09: 상품관리 - 상품별 정보 조회 - 상품별로 이름, 이미지, 단가, 총 주문 수량을 출력한다.

private const val TAG = "MenuDetailActivity_싸피"

class MenuDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuDetailBinding
    private lateinit var storeRepository: StoreRepository
    private lateinit var commentList: List<Comment>
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var user: String
    private lateinit var productId: String
    private var priceOne: Int = 0
    private var img: String = ""

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        binding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId").toString()
        user = intent.getStringExtra("UserID").toString()

        storeRepository = StoreRepository.get()

        binding.menuDetailPlus.setOnClickListener {
            var num = binding.menuDetailCount.text.toString().toInt() + 1
            binding.menuDetailCount.text = num.toString()
            var price = priceOne * num
            binding.menuDetailCartButton.text = "Add to Cart Won. "+price.toString()
        }
        binding.menuDetailMinus.setOnClickListener {
            if (binding.menuDetailCount.text.toString().toInt() > 1) {
                var num = binding.menuDetailCount.text.toString().toInt() - 1
                binding.menuDetailCount.text = num.toString()
                var price = priceOne * num
                binding.menuDetailCartButton.text = "Add to Cart Won. "+price.toString()
            }
        }
        binding.menuDetailRatingbar.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, rating, fromUser -> // 저는 0개를 주기싫어서, 만약 1개미만이면 강제로 1개를 넣었습니다.
                binding.menuDetailRatingTextview.text =
                    binding.menuDetailRatingbar.rating.toString() + "점"
            }
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {

                val product = storeRepository.getProduct(productId.toString().toInt())
                img = product?.img?.replace(".png", "").toString()
                val resource = resources.getIdentifier(
                    img,
                    "drawable",
                    packageName
                )
                this@MenuDetailActivity.runOnUiThread(Runnable {
                    val drawable = context.getDrawable(R.drawable.img_item) as GradientDrawable?
                    binding.menuDetailImg.background = drawable
                    binding.menuDetailImg.clipToOutline = true
                    binding.menuDetailImg.setImageResource(resource)
                    binding.menuDetailName.text = product?.name
                    priceOne = product?.price!!
                    binding.menuDetailPrice.text = priceOne.toString()+"원"
                    binding.menuDetailCartButton.text = "Add to Cart Won. "+priceOne.toString()
                })

            }
        }

        binding.menuDetailCartButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("productId", productId)
            intent.putExtra("price", priceOne.toString())
            intent.putExtra("count", binding.menuDetailCount.text.toString())
            intent.putExtra("name", binding.menuDetailName.text.toString())
            intent.putExtra("img", img)
            setResult(Activity.RESULT_OK, intent)
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
            finish()
        }

        //코멘트
        updateListView()

        binding.menuDetailCommentButton.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val job = launch {
                    var comment = binding.menuDetailCommentEdit.text.toString()
                    var rating = binding.menuDetailRatingbar.rating
                    storeRepository.insertComment(Comment(user, productId.toInt(), rating, comment))
                    Log.d("코멘트 등록 : ", "true")
                }
                job.join()
                delay(100L)
                launch {
                    Log.d("드가자~ : ", "1")
                    updateListView()
                    Log.d("나가자~ : ", "4")
                    this@MenuDetailActivity.runOnUiThread(Runnable {
                        binding.menuDetailCommentEdit.setText(
                            ""
                        )
                    })
                }
            }

        }


    }

    private fun updateListView() {
        CoroutineScope(Dispatchers.Default).launch {
            val job = launch {
                commentList = storeRepository.getProductComments(productId.toString().toInt())!!
                Log.d("코멘트 리스트 : ", commentList.toString())
            }
            job.join()
            val job2 = launch {
                commentAdapter = CommentAdapter(this@MenuDetailActivity, commentList, user)
                Log.d("어댑터 셋팅 : ", "2")
                commentAdapter.setItemClickListener(object : CommentAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {
                        val editText = EditText(this@MenuDetailActivity)
                        editText.setText(commentList[position].comment)
                        val alertDialogBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(this@MenuDetailActivity, R.style.MyDialogTheme)

                        alertDialogBuilder
                            .setMessage("댓글 변경")
                            .setCancelable(false)
                            .setView(editText)
                            .setPositiveButton("취소") { dialog, id -> // 다이얼로그를 취소한다

                            }
                            .setNegativeButton(
                                "변경"
                            ) { dialog, id ->
                                CoroutineScope(Dispatchers.Default).launch {
                                    launch {
                                        commentList[position].comment = editText.text.toString()
                                        storeRepository.updateComment(commentList[position])
                                    }.join()
                                    delay(100L)
                                    updateListView()
                                }
                            }


                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()

                    }
                }, 1)

                commentAdapter.setItemClickListener(object : CommentAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {
                        CoroutineScope(Dispatchers.Default).launch {
                            launch {
                                storeRepository.deleteComment(commentList[position].id)
                            }.join()
                            delay(100L)
                            updateListView()
                        }
                    }
                }, 0)

            }
            job2.join()
            val job3 = launch {
                this@MenuDetailActivity.runOnUiThread(Runnable {
                    binding.menuDetailRecyclerview.apply {
                        adapter = commentAdapter
                        commentAdapter.notifyDataSetChanged()
                        Log.d("어댑터 실행 : ", "3")
                    }
                })
            }
            job3.join()
        }
    }
}