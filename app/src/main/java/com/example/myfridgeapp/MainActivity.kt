package com.example.myfridgeapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myfridgeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val PREFSNAME = "UserInfo"
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkFirstTime()


    }

    private fun checkFirstTime() {
        val pref = getSharedPreferences(PREFSNAME, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        if(pref.contains("FIRST_SETUP")){ // 첫 세팅이 아닐 경우
            val intent = Intent(this, FridgeManager::class.java)
            startActivity(intent)
            finish()
        }else{ // 첫 세팅일 경우
            val intent = Intent(this, FirstLaunchActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}