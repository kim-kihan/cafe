package com.ssafy.smartstore

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.databinding.FragmentMyPageBinding
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyPageFragment : Fragment() {
    private lateinit var ctx: Context

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    private lateinit var listViewItems: List<Map<String, Object>>
    private lateinit var storeRepository: StoreRepository
    private lateinit var mypageAdapater: MyPageAdapter
    private lateinit var UserName: String
    private lateinit var UserId: String
    private var UserStamps: Int = 0
    private lateinit var userStampsLevel: String
    private var levelDetail: String = ""
    private var levelDetailElse: String = ""
    private var levelAlarm: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        mainActivity = context as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UserName = arguments?.getString("UserName").toString()
        UserId = arguments?.getString("UserID").toString()

        val drawable = ctx.getDrawable(R.drawable.img_item) as GradientDrawable?
        binding.myPageUserImg.background = drawable
        binding.myPageUserImg.clipToOutline = true

        storeRepository = StoreRepository.get()
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                UserStamps = storeRepository.getUserStamp(UserId)?.stamps!!
                Log.d("TAG", "onViewCreatedasd: $UserStamps")
            }.join()
            delay(100L)
            launch {
                Log.d("스탬프 개수 : ", UserStamps.toString())
                mainActivity.runOnUiThread(Runnable {
                    when (UserStamps) {
                        in 0..50 -> {
                            userStampsLevel = "씨앗"
                            levelDetail = (UserStamps / 10).toString() + "단계"
                            levelDetailElse = (UserStamps % 10).toString() + "/10"
                            levelAlarm = (10 - (UserStamps % 10)).toString()
                            binding.progressBar.progress = (((UserStamps % 10).toDouble()/10.0) * 100.0).toInt()
                        }
                        in 51..125 -> {
                            userStampsLevel = "꽃"
                            levelDetail = ((UserStamps - 50) / 15).toString() + "단계"
                            levelDetailElse = (UserStamps % 15).toString() + "/15"
                            levelAlarm = (15 - (UserStamps % 15)).toString()
                            binding.progressBar.progress = (((UserStamps % 15).toDouble()/15.0) * 100.0).toInt()
                        }
                        in 126..225 -> {
                            userStampsLevel = "열매"
                            levelDetail = ((UserStamps - 125) / 20).toString() + "단계"
                            levelDetailElse = (UserStamps % 20).toString() + "/20"
                            levelAlarm = (20 - (UserStamps % 20)).toString()
                            binding.progressBar.progress = (((UserStamps % 20).toDouble()/20.0) * 100.0).toInt()
                        }
                        in 226..350 -> {
                            userStampsLevel = "커피콩"
                            levelDetail = ((UserStamps - 225) / 25).toString() + "단계"
                            levelDetailElse = (UserStamps % 25).toString() + "/25"
                            levelAlarm = (25 - (UserStamps % 25)).toString()
                            binding.progressBar.progress = (((UserStamps % 25).toDouble()/25.0) * 100.0).toInt()
                        }
                        else -> userStampsLevel = "나무"
                    }
                    binding.myPageUserName.text = "${UserName}님"

                    binding.myPageUserLevel.text = userStampsLevel + " " + levelDetail
                    binding.myPageUserLevelCount.text = levelDetailElse
                    binding.myPageUserLevelAlarm.text = "다음 레벨까지 " + levelAlarm + "잔 남았습니다."
                })

            }.join()
            delay(100L)
            launch {
                listViewItems = storeRepository.getOrder(UserId)!!
                var preOId = -1
                var a: MutableList<Map<String, Object>> = mutableListOf()
                var count = 1
                var countList: MutableList<Int> = mutableListOf()
                for (i in listViewItems) {
                    var oid = i["o_id"].toString().toDouble().toInt()
                    Log.d("oid : ", oid.toString())
                    if (preOId != oid) {
                        count = 1
                        a.add(i)
                        preOId = oid
                        countList.add(count)

                    } else {
                        countList.removeAt(countList.lastIndex)
                        count++
                        countList.add(count)
                    }
                }
                Log.d("aaaa : ", listViewItems.toString())
                mypageAdapater = MyPageAdapter(mainActivity, a, mainActivity, countList)
                mainActivity.runOnUiThread(Runnable {
                    binding.myPageRecyclerview.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = mypageAdapater
                    }
                })
            }
        }



        binding.myPageExit.setOnClickListener {
            Intent(ctx, LoginActivity::class.java).apply {
                settext("로그아웃 되었습니다.")
                startActivity(this)
                requireActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
            }
        }


    }

    fun settext(text: String){
        var toastLayout = layoutInflater.inflate(com.ssafy.smartstore.R.layout.toast_layout, null)

        var textView: TextView? = toastLayout.findViewById(com.ssafy.smartstore.R.id.toast)
        textView?.text = text
        toastLayout.setBackgroundResource(com.ssafy.smartstore.R.drawable.toast_back)

        var toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT)

        toast.view = toastLayout
        toast.show()
    }
}

