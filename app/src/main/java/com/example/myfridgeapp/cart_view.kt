package com.example.myfridgeapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfridgeapp.databinding.CartViewBinding
import com.example.refrigerator_manage.CartData
import java.util.*

class cart_view : AppCompatActivity() {
    lateinit var binding: CartViewBinding
    val data: ArrayList<CartData> = ArrayList()
    lateinit var cartItemAdapter: MyCartItemAdapter
    lateinit var myCartDBHelper: MyCartDBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    var message = " 제품의 유통기한이 곧 만료됩니다!!!!!! \n확인해보세요!"

    fun makeNotification(pname:String){
        val id = "MyChannel"
        val name = "TimeCheckChannel"

        val notificationChannel = NotificationChannel(id,name, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableVibration(true)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val builder = NotificationCompat.Builder(this,id)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentTitle("유통기한 만기 알림")
            .setContentText(pname+message)
            .setAutoCancel(true)

        val intent = Intent(this,cart_view::class.java)
        intent.putExtra("time",message)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(this,
            1,intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val notification = builder.build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
        manager.notify(10,notification)
    }

    private fun init() {
        myCartDBHelper = MyCartDBHelper(this)
        myCartDBHelper.insertProduct(CartData(0,"당근",2))
        myCartDBHelper.savetorecycler()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerview.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        cartItemAdapter = MyCartItemAdapter(data)
        cartItemAdapter.increaseLinstener = object : MyCartItemAdapter.OnItemClickListener {
            override fun OnItemClick(data: CartData, pos: Int) {
                var now_amt = data.menuCnt
                var now_pid = data.pid
                var now_name = data.nameMenu

                now_amt++
                data.menuCnt++
                var new_menu = CartData(now_pid, now_name, now_amt)
                val result = myCartDBHelper.updateProduct(new_menu)
                if (result) {
                    Toast.makeText(this@cart_view, "Data UPDATE SUCCESS", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@cart_view, "Data UPDATE FAILED", Toast.LENGTH_SHORT)
                        .show()
                }
                cartItemAdapter.notifyItemChanged(pos)
            }
        }

        cartItemAdapter.decreaseListener = object : MyCartItemAdapter.OnItemClickListener {
            override fun OnItemClick(data: CartData, pos: Int) {
                var now_amt = data.menuCnt
                var now_pid = data.pid
                var now_name = data.nameMenu

                now_amt--
                var new_menu = CartData(now_pid, now_name, now_amt)

                val result = myCartDBHelper.updateProduct(new_menu)
                if (result) {
                    Toast.makeText(this@cart_view, "Data UPDATE SUCCESS", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@cart_view, "Data UPDATE FAILED", Toast.LENGTH_SHORT)
                        .show()
                }
                if (now_amt == 0) {
                    val result = myCartDBHelper.deleteProduct(now_pid.toString())
                    if (result) {

                        Toast.makeText(
                            this@cart_view,
                            "Data DELETE SUCCESS",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@cart_view,
                            "Data DELETE FAILED",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    cartItemAdapter.removeItem(pos);
                } else {
                    data.menuCnt--
                }
                cartItemAdapter.notifyItemChanged(pos)

            }

        }
        cartItemAdapter.hrefListener = object : MyCartItemAdapter.OnItemClickListener {
            override fun OnItemClick(data: CartData, pos: Int) {
                //구매 링크 이동
                var now_item_name = data.nameMenu
                var uri_smaple = "https://msearch.shopping.naver.com/search/all?query="
                var full_uri = uri_smaple + now_item_name
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(full_uri))
                startActivity(intent)
                cartItemAdapter.notifyItemChanged(pos)
            }
        }
        cartItemAdapter.deleteListener = object : MyCartItemAdapter.OnItemClickListener {
            override fun OnItemClick(data: CartData, pos: Int) {
                var now_pid = data.pid

                val result = myCartDBHelper.deleteProduct(now_pid.toString())
                if (result) {

                    Toast.makeText(this@cart_view, "Data DELETE SUCCESS", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@cart_view, "Data DELETE FAILED", Toast.LENGTH_SHORT)
                        .show()
                }
                cartItemAdapter.removeItem(pos);
                cartItemAdapter.notifyItemChanged(pos)

            }

        }
        binding.recyclerview.adapter = cartItemAdapter
    }
}