package com.example.myfridgeapp

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.myfridgeapp.databinding.ActivityFridgeManagerBinding

class FridgeManager : AppCompatActivity() {
    lateinit var fridgeListDB: FridgeListDB
    lateinit var binding: ActivityFridgeManagerBinding
    private var isFabOpen = false
    private lateinit var fridgeList : List<FridgeData>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFridgeManagerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDB()
        initFridge()
        initFloatingBtn()
        initViewPager()
    }

    private fun initDB() {
        fridgeListDB = FridgeListDB(this)
    }

    private fun initViewPager() {
        val adapter = OuterFridgeAdapter(fridgeList)
        val viewPager = binding.viewPager
        viewPager.adapter = adapter
    }

    //fridge db에서 가져오기
    private fun initFridge() {
        fridgeList = fridgeListDB.getFridgeList()
        Log.d("name1", fridgeList.size.toString())
        for(i in fridgeList){
            Log.d("name1", i.name)
        }
    }

    private fun initFloatingBtn(){
        setFABClickEvent()
    }

    private fun setFABClickEvent() {
        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        binding.fabMain.setOnClickListener {
            toggleFab()
        }

        // 플로팅 버튼 클릭 이벤트 - 바코드 인식 혹은 제품 추가
        binding.fabScanbarcode.setOnClickListener {
            val intent = Intent(this,BarcodeScan::class.java)
            startActivity(intent)
        }

        binding.fabCart.setOnClickListener {
            val intent = Intent(this,cart_view::class.java)
            startActivity(intent)
        }
        // 플로팅 버튼 클릭 이벤트 - 냉장고 추가
        binding.fabAddfridge.setOnClickListener {
            val intent = Intent(this, AddFridge::class.java)
            startActivity(intent)
        }


    }

    private fun toggleFab() {
        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.fabCart, "translationY", 0f).start()
            ObjectAnimator.ofFloat(binding.fabAddfridge, "translationY", 0f).start()
            ObjectAnimator.ofFloat(binding.fabScanbarcode, "translationY", 0f).start()
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 45f, 0f).start()
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(binding.fabCart, "translationY", -540f).start()
            ObjectAnimator.ofFloat(binding.fabAddfridge, "translationY", -360f).start()
            ObjectAnimator.ofFloat(binding.fabScanbarcode, "translationY", -180f).start()
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 0f, 45f).start()
        }

        isFabOpen = !isFabOpen

    }
}