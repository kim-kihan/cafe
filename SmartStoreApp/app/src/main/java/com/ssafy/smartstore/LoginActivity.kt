package com.ssafy.smartstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.ssafy.smartstore.databinding.ActivityLoginBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

// F04: 회원 관리 - 회원 로그인 - 추가된 회원 정보를 이용해서 로그인 할 수 있다. 로그아웃을 하기 전까지 앱을 실행시켰 을 때 로그인이 유지되어야 한다.
// F05: 회원 관리 - 회원 로그아웃

private const val TAG = "LoginActivity_싸피"
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var storeRepository: StoreRepository


    fun settext(text: String){
        var toastLayout = layoutInflater.inflate(R.layout.toast_layout, null)

        var textView: TextView? = toastLayout.findViewById(R.id.toast)
        textView?.text = text
        toastLayout.setBackgroundResource(R.drawable.toast_back)

        var toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        toast.view = toastLayout
        toast.show()
    }

    private fun move(user: User){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("User", user)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeRepository = StoreRepository.get()



        binding.buttonLogin.setOnClickListener {
            if (binding.loginId.text.isEmpty() || binding.loginPw.text.isEmpty()) {
                settext("아이디와 비밀번호를 입력해주세요.")
            } else {
                //로그인 기능 추가
                CoroutineScope(Dispatchers.IO).launch{
                    try {
                        storeRepository.getUser(User(binding.loginId.text.toString(), "",binding.loginPw.text.toString(),0))
                            ?.let { it1 -> move(it1) }
                    } catch (e: Exception){
                        this@LoginActivity.runOnUiThread(Runnable {
                            settext("아이디와 비밀번호를 확인해주세요.")
                        })
                    }

                }
            }
        }
        binding.buttonLoginJoin.setOnClickListener {

            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }
    }


}