package com.example.myfridgeapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.myfridgeapp.databinding.ActivityAddFridgeBinding

import java.util.*

class AddFridge : AppCompatActivity() {
    private val PREFSNAME = "UserInfo"
    lateinit var fridgeListDB: FridgeListDB
    lateinit var binding : ActivityAddFridgeBinding
    lateinit var editTexts : Array<EditText>
    lateinit var layouts : Array<LinearLayout>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddFridgeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDB()
        initLayout()
    }

    private fun initDB(){
        fridgeListDB = FridgeListDB(this)
    }

    private fun initLayout() {
        binding.apply{
            editTexts = arrayOf(floorText,nameText)
            layouts = arrayOf(firstLayout,secondLayout,lastLayout)
        }
        editTexts.forEachIndexed{
            i,element->

            var timer = Timer()
            element.addTextChangedListener{
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask(){
                        override fun run() {
                            runOnUiThread {
                                if(i < layouts.size - 1)
                                    layouts[i+1].visibility = View.VISIBLE
                            }
                        }
                    }, 1000
                )
            }
            element.setOnClickListener {
                var idx = i
                while(idx < layouts.size - 1){
                    layouts[++idx].visibility = View.GONE
                }
            }
            element.setOnFocusChangeListener {
                    view, b ->
                if(b){
                    element.backgroundTintList = ColorStateList.valueOf(Color.BLUE)
                }else{
                    element.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }

            }
        }

        binding.finishBtn.setOnClickListener {
            val pref = getSharedPreferences(PREFSNAME, MODE_PRIVATE)
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putBoolean("FIRST_SETUP", true)
            editor.commit()

            val fridge = FridgeData(
                binding.nameText.text.toString(),
                binding.floorText.text.toString().toInt()
            )
            val result = fridgeListDB.insertFridge(fridge)
            if(result){
                val intent = Intent(this, FridgeManager::class.java)
                intent.putExtra("fridgeData", fridge)
                startActivity(intent)
                setResult(RESULT_OK, null)

                finish()
            }else{
                Toast.makeText(this,"가상 냉장고를 만들지 못했습니다. 잠시 후에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}