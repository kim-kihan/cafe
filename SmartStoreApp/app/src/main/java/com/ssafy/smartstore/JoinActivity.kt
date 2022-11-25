package com.ssafy.smartstore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.NumberPicker.OnValueChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstore.databinding.ActivityJoinBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// F02: 회원 관리 - 회원 정보 추가 회원 가입 - 회원 정보를 추가할 수 있다.
// F03: 회원 관리 - 회원 아이디 중복 확인 - 회원 가입 시 아이디가 중복되는지 여부를 확인할 수 있다.

private const val TAG = "JoinActivity_싸피"
class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding
    private var checkJoinID = false
    private lateinit var storeRepository: StoreRepository
    private var year: String = ""
    private var month: String = ""
    private var day: String = ""
    private var gender: String = ""


    fun settext(text: String){
        var toastLayout = layoutInflater.inflate(R.layout.toast_layout, null)

        var textView: TextView? = toastLayout.findViewById(R.id.toast)
        textView?.text = text
        toastLayout.setBackgroundResource(R.drawable.toast_back)

        var toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        toast.view = toastLayout
        toast.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeRepository = StoreRepository.get()

        binding.numberPickerYaer.maxValue = 2022
        binding.numberPickerYaer.minValue = 1960
        binding.numberPickerYaer.value = 1980
        year = binding.numberPickerYaer.value.toString()

        binding.numberPickerYaer.setOnValueChangedListener(OnValueChangeListener { numberPicker, oldValue, newValue ->
            year = newValue.toString()
        })

        binding.numberPickerMonth.maxValue = 12
        binding.numberPickerMonth.minValue = 1
        binding.numberPickerMonth.value = 6
        month = "0" + binding.numberPickerMonth.value.toString()

        binding.numberPickerMonth.setOnValueChangedListener(OnValueChangeListener { numberPicker, oldValue, newValue ->
            month = if(newValue<10){
                "0$newValue"
            }else{
                newValue.toString()
            }
        })

        binding.numberPickerDay.maxValue = 31
        binding.numberPickerDay.minValue = 1
        binding.numberPickerDay.value = 15
        day = binding.numberPickerDay.value.toString()

        binding.numberPickerDay.setOnValueChangedListener(OnValueChangeListener { numberPicker, oldValue, newValue ->
            day = if(newValue<10){
                "0$newValue"
            }else{
                newValue.toString()
            }
        })

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> Log.d("선택 안함", "선택 안함")
                    1 -> gender = "m"
                    else -> gender = "w"
                }
            }
        }


        binding.idCheckImg.setOnClickListener{
            if(binding.joinId.text.isEmpty()){
                settext("아이디를 입력해주세요.")
            } else{
                CoroutineScope(Dispatchers.IO).launch{
                    var checkId = storeRepository.getUserId(binding.joinId.text.toString())
                    checkJoinID = if(checkId == true) {
                        this@JoinActivity.runOnUiThread(Runnable {
                            settext("중복된 아이디입니다.")
                        })
                        false
                    } else {
                        //만약 충복체크 시, 중복X 이면
                        this@JoinActivity.runOnUiThread(Runnable {
                            settext("중복되지 않은 아이디입니다.")
                        })

                        true
                    }
                }
            }
        }

        binding.buttonJoin.setOnClickListener {
            Log.d(TAG, "onCreate: test")
            if(checkJoinID){
                if(binding.joinId.text.isEmpty() || binding.joinPw.text.isEmpty()
                    || binding.joinName.text.isEmpty()){
                    settext("빈칸을 모두 채워주세요.")
                } else{
                    CoroutineScope(Dispatchers.IO).launch{
                        val age = "$year-$month-$day"
                        storeRepository.insertUser(User(binding.joinId.text.toString(),
                            binding.joinName.text.toString(), binding.joinPw.text.toString(), 0, age, gender))
                    }
                    settext("회원가입 성공")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
                    finish()
                }
            } else{
                settext("아이디 중복 여부를 체크해주세요.")
            }


            if(binding.joinId.text.toString() != ""){
                if(checkJoinID){
                    if(binding.joinPw.text.toString() != ""){
                        if(binding.joinName.text.toString() != ""){
                            //로그인 기능 추가
                            if(gender != ""){
                                //로그인 기능 추가
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                Toast.makeText(this,"성별을 입력해 주세요", Toast.LENGTH_SHORT)
                            }
                        }else{
                            Toast.makeText(this,"별명을 입력해 주세요", Toast.LENGTH_SHORT)
                        }
                    }else{
                        Toast.makeText(this,"PW를 입력해 주세요", Toast.LENGTH_SHORT)
                    }
                }else{
                    Toast.makeText(this,"중복여부를 체크 해 주세요", Toast.LENGTH_SHORT)
                }

            }else{
                Toast.makeText(this,"ID를 입력해 주세요", Toast.LENGTH_SHORT)
            }
        }

        binding.backImg.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
            finish()
        }
    }
}