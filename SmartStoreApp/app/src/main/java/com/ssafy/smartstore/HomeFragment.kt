package com.ssafy.smartstore

import android.content.Context
import android.content.Intent
import android.media.MediaExtractor
import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstore.databinding.FragmentHomeBinding
import com.ssafy.smartstore.databinding.FragmentOrderBinding
import com.ssafy.smartstore.dto.MessageDTO
import com.ssafy.smartstore.dto.OrderProduct
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.firebase.NoticeAdapter
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconConsumer
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(){

    private lateinit var ctx: Context

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    private lateinit var listViewItems: List<Map<String, Object>>
    private lateinit var storeRepository: StoreRepository
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var UserName: String
    private lateinit var UserId: String

    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var notices: ArrayList<MessageDTO>

    private lateinit var noticeArr: List<MessageDTO>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var UserName = arguments?.getString("UserName")
        var UserId = arguments?.getString("UserID").toString()
        var userStamps = arguments?.getString("UserStamps").toString()

        if(StoreApplicationClass.prefs.checkString("messageList", "none")){
            notices = StoreApplicationClass.prefs.getString("messageList", "none")
        } else{
            notices = ArrayList()
        }
        noticeAdapter = NoticeAdapter(notices)
        noticeAdapter.setItemClickListener(object : NoticeAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                notices.removeAt(position)
                StoreApplicationClass.prefs.setString("messageList", notices)
                updateListView()
            }
        })
        binding.noticeRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = noticeAdapter
        }

        binding.tvHomeId.text = UserName

        storeRepository = StoreRepository.get()
        CoroutineScope(Dispatchers.IO).launch{
            val job = launch {
                listViewItems = storeRepository.getOrder(UserId)!!
            }
            job.join()
            delay(200L)
            launch {
                var preOId = -1
                var a:  MutableList<Map<String, Object>> = mutableListOf()
                var count = 1
                var countList: MutableList<Int> = mutableListOf()
                for(i in listViewItems){
                    var oid = i["o_id"].toString().toDouble().toInt()
                    if(preOId != oid){
                        count=1
                        a.add(i)
                        preOId = oid
                        countList.add(count)

                    }else{
                        countList.removeAt(countList.lastIndex)
                        count++
                        countList.add(count)
                    }
                }

                homeAdapter = HomeAdapter(mainActivity, a, mainActivity, countList, UserName, UserId, userStamps)
            }.join()
            delay(100L)
            launch {
                mainActivity.runOnUiThread(Runnable {
                    binding.homeRecyclerview.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = homeAdapter
                    }
                })
            }

        }

    }


    fun updateListView() {
        if(StoreApplicationClass.prefs.checkString("messageList", "none")){
            notices = StoreApplicationClass.prefs.getString("messageList", "none")
        } else{
            notices = ArrayList()
        }

        noticeAdapter = NoticeAdapter(notices)
        noticeAdapter.notifyDataSetChanged()

        noticeAdapter.setItemClickListener(object : NoticeAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                notices.removeAt(position)
                StoreApplicationClass.prefs.setString("messageList", notices)
                updateListView()
            }
        })

        binding.noticeRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = noticeAdapter
        }
    }
}