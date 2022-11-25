package com.ssafy.smartstore

import android.Manifest
import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ssafy.smartstore.databinding.FragmentSearchBinding
import com.ssafy.smartstore.dto.Search
import com.ssafy.smartstore.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment(){

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var ctx: Context
    private lateinit var activity: MainActivity
    private lateinit var userID: String
    private lateinit var userName: String
    private lateinit var userStamps: String

    val PERMISSION = 1

    private lateinit var storeRepository: StoreRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        activity = ctx as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userID = arguments?.getString("UserID").toString()
        userName = arguments?.getString("UserName").toString()
        userStamps = arguments?.getString("UserStamps").toString()

        binding.searchFragmentSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 버튼 누를 때 호출
                CoroutineScope(Dispatchers.Main).launch {
                    var content = storeRepository.insertSearch(Search(userID,query))

                }
                val intent = Intent(context, SearchResultActivity::class.java)
                intent.putExtra("content",query)
                intent.putExtra("UserID",userID)
                intent.putExtra("UserName",userName)
                intent.putExtra("UserStamps",userStamps)
                ActivityCompat.startActivityForResult(activity, intent,300, null)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                // 검색창에서 글자가 변경이 일어날 때마다 호출

                return true
            }
        })
        storeRepository = StoreRepository.get()

        CoroutineScope(Dispatchers.Main).launch {
            var content = storeRepository.getSearch(userID)
            var list: MutableList<String> = mutableListOf()
            if (content != null) {
                for(i in content){
                    i.content?.let { list.add(it) }
                }
            }
            if(list.size > 0){
                list = list.distinct() as MutableList<String>
            }
            activity.runOnUiThread (Runnable {
                binding.listViewSearch.apply {
                    adapter =
                        ArrayAdapter(activity, R.layout.simple_list_item_1, list)
                    onItemClickListener =
                        OnItemClickListener { adapterView, view, position, id -> //클릭한 아이템의 문자열을 가져옴
                            val selectedItem = adapterView.getItemAtPosition(position) as String
                            binding.searchFragmentSearch.setQuery(selectedItem, true)
                        }
                }
            })
        }

        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO
            ), PERMISSION
        )

        // RecognizerIntent 객체 생성
        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        val listener: RecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                settext("음성인식을 시작합니다.")
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                val message: String
                message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER가 바쁨"
                    SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                    else -> "알 수 없는 오류임"
                }
                val guideStr = "에러가 발생하였습니다."
                settext(guideStr + message)
            }

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                var resultStr = ""
                for (i in 0 until matches!!.size) {
                    resultStr += matches!![i]
                }
                if (resultStr.isEmpty()) return
                resultStr = resultStr.replace(" ", "")
                binding.searchFragmentSearch.setQuery(resultStr, true)
            }

            override fun onPartialResults(partialResults: Bundle) {}
            override fun onEvent(eventType: Int, params: Bundle) {}
        }

        // 버튼을 클릭 이벤트 - 객체에 Context와 listener를 할당한 후 실행
        binding.imageView2.setOnClickListener { v ->
            var mRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(ctx)
            mRecognizer.setRecognitionListener(listener)
            mRecognizer.startListening(intent)
        }

    }
}