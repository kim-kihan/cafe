package com.ssafy.smartstore.repository

import android.content.Context
import android.util.Log
import com.ssafy.smartstore.StoreApplicationClass
import com.ssafy.smartstore.database.*
import com.ssafy.smartstore.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SmartStore_싸피"
private const val DATABASE_NAME = "store-database.db"

class StoreRepository private constructor(context: Context) {

    private val UserDao = StoreApplicationClass.retrofit.create(UserDao::class.java)
    private val ProductDao = StoreApplicationClass.retrofit.create(ProductDao::class.java)
    private val OrderDao = StoreApplicationClass.retrofit.create(OrderDao::class.java)
    private val OrderDetailDao = StoreApplicationClass.retrofit.create(OrderDetailDao::class.java)
    private val CommentDao = StoreApplicationClass.retrofit.create(CommentDao::class.java)
    private val StampDao = StoreApplicationClass.retrofit.create(StampDao::class.java)
    private val SearchDao = StoreApplicationClass.retrofit.create(SearchDao::class.java)

    fun insertSearch(dto: Search){
        SearchDao.insert(dto).enqueue(object :
            Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            "완료",
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "댓글정보를 저장할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d(TAG, t.message ?: "댓글정보를 저장하는 중 통신오류")
            }
        })
    }

    suspend fun getSearch(userId: String): List<Search>? {
        var commentList: List<Search>? = withContext(Dispatchers.IO) {
            SearchDao.select(userId).execute().body()
        }


        return commentList
    }

    fun insertStamp(dto: Stamp) {
        StampDao.insert(dto).enqueue(object :
            Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "댓글정보를 저장할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d(TAG, t.message ?: "댓글정보를 저장하는 중 통신오류")
            }
        })
    }


    suspend fun getProductComments(productID: Int): List<Comment>? {
        var commentList: List<Comment>? = withContext(Dispatchers.IO) {
            CommentDao.selectByProduct(productID).execute().body()
        }


        return commentList
    }

    suspend fun getComments(): List<Comment>? {
        var commentList: List<Comment>? = withContext(Dispatchers.IO) {
            CommentDao.selectAll().execute().body()
        }


        return commentList
    }

    fun insertComment(dto: Comment) {
        CommentDao.insert(dto).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "댓글정보를 저장할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "댓글정보를 저장하는 중 통신오류")
            }
        })
    }

    fun updateComment(dto: Comment) {
        CommentDao.update(dto).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "댓글정보를 수정할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "댓글정보를 수정하는 중 통신오류")
            }
        })
    }

    fun deleteComment(id: Int) {
        CommentDao.delete(id).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "댓글정보를 삭제할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "댓글정보를 삭제하는 중 통신오류")
            }
        })
    }

    fun updateUser(dto: User) {
        UserDao.update(dto).enqueue(object :
            Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "댓글정보를 수정할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d(TAG, t.message ?: "댓글정보를 수정하는 중 통신오류")
            }
        })
    }

    suspend fun getUser(userCheck: User): User? {
        var user: User? = withContext(Dispatchers.IO) {
            UserDao.login(userCheck).execute().body()
        }

        return user
    }

    suspend fun getUserAge(userCheck: String): User? {
        var user: User? = withContext(Dispatchers.IO) {
            UserDao.checkAge(userCheck).execute().body()
        }

        return user
    }

    suspend fun getUserStamp(userCheck: String): User? {
        var user: User? = withContext(Dispatchers.IO) {
            UserDao.checkStamps(userCheck).execute().body()
        }

        return user
    }

    suspend fun getUserGender(userCheck: String): User? {
        var user: User? = withContext(Dispatchers.IO) {
            UserDao.checkGender(userCheck).execute().body()
        }

        return user
    }

    suspend fun getUserStamps(userId: String): List<Stamp>? {
        var stampList: List<Stamp>? = withContext(Dispatchers.IO) {
            StampDao.selectByUserId(userId).execute().body()
        }


        return stampList
    }

    // 아이디 중복확인
    suspend fun getUserId(id: String): Boolean? {
        var UserIdCheck: Boolean? = withContext(Dispatchers.IO) {
            UserDao.checklogin(id).execute().body()
        }

        return UserIdCheck
    }


    fun insertUser(dto: User) {
        UserDao.insert(dto).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "회원가입 할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "회원가입 하는 중 통신오류")
            }
        })
    }

    suspend fun getOrders(): List<Order>? {
        var orderList: List<Order>? = withContext(Dispatchers.IO) {
            OrderDao.selectAll().execute().body()
        }

        return orderList
    }


    suspend fun getOrder(id: String): List<Map<String, Object>>? {
        var orderList: List<Map<String, Object>>? = withContext(Dispatchers.IO) {
            OrderDao.getLastMonthOrder(id).execute().body()
        }

        return orderList
    }


    fun insertOrder(dto: Order) {
        OrderDao.makeOrder(dto).enqueue(object :
            Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "주문정보를 저장할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d(TAG, t.message ?: "주문정보를 저장하는 중 통신오류")
            }
        })
    }

    suspend fun getOrderDetail(orderId: Int): List<Map<String, Object>>? {
        var orderDetailList: List<Map<String, Object>>? = withContext(Dispatchers.IO) {
            OrderDao.getOrderDetail(orderId).execute().body()
        }
        return orderDetailList
    }

    suspend fun getOrderDetails(orderId: Int): List<OrderDetail>? {
        var orderDetailList: List<OrderDetail>? = withContext(Dispatchers.IO) {
            OrderDetailDao.select(orderId).execute().body()
        }
        return orderDetailList
    }


    fun insertOrderDetail(dto: OrderDetail) {
        OrderDetailDao.insert(dto).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "주문상세정보를 저장할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "주문상세정보를 저장하는 중 통신오류")
            }
        })
    }


    suspend fun getProducts(): List<Product>? {
        var productList: List<Product>? = withContext(Dispatchers.IO) {
            ProductDao.getProductList().execute().body()
        }

        return productList

    }


    suspend fun getProduct(id: Int): Product? {
        var product: Product? = withContext(Dispatchers.IO) {
            ProductDao.getProduct(id).execute().body()
        }

        return product
    }

    fun updateProduct(dto: Product) {
        ProductDao.update(dto).enqueue(object :
            Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        Log.d(
                            TAG,
                            res.toString()
                        )
                    } else {
                        Log.d(
                            TAG,
                            "댓글정보를 수정할 수 없습니다."
                        )
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d(TAG, t.message ?: "댓글정보를 수정하는 중 통신오류")
            }
        })
    }

    companion object {
        private var INSTANCE: StoreRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = StoreRepository(context)
            }
        }

        fun get(): StoreRepository {
            return INSTANCE ?: throw IllegalStateException("StoreRepository must be initialized")
        }
    }
}