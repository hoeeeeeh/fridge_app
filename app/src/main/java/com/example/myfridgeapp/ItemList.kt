package com.example.myfridgeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridgeapp.databinding.ActivityItemListBinding
import com.example.myfridgeapp.databinding.FridgeColumnBinding
import com.example.myfridgeapp.databinding.ItemRowBinding

class ItemList : AppCompatActivity() {
    private lateinit var productList : MutableList<Product>
    private var selectedFloor : Int = -1
    lateinit var fridgeInsideDB : MyDBHelper
    lateinit var fridge : FridgeData
    lateinit var binding : ActivityItemListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityItemListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDB()
        initFridgeData()
        initLayout()
    }

    private fun initDB() {
        fridgeInsideDB = MyDBHelper(this)
    }

    private fun initLayout() {
        binding.apply{
            floorItemList.text = "${fridge.name} ${selectedFloor}층"
            productListRecyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
            productListRecyclerView.adapter = SelectedFloorAdapter(productList)
        }
    }

    fun initFridgeData() {
        if(intent.getSerializableExtra("fridge") != null) {
            fridge = intent.getSerializableExtra("fridge") as FridgeData
            Log.d("LogTest : Fridge", "${fridge.fid} ${fridge.floor}")
            selectedFloor = intent.getIntExtra("selectedFloor", -1)
            Log.d("LogTest : Fridge", "${fridge.name}, ${selectedFloor}")

            productList = fridgeInsideDB.getProduct(fridge.name, selectedFloor)
        }else{
            Toast.makeText(this, "냉장고 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }

        for(i in productList){
            Log.d("LogTest : ProductList","${i.pname} ${i.fName} ${i.fFloor}층 ${i.pquantity} ${i.expdate}")
        }
    }
}

class SelectedFloorAdapter(val productList: List<Product>) : RecyclerView.Adapter<SelectedFloorAdapter.ViewHolder>(){

    interface OnItemClickListener{
        // fun OnItemClick(data: MyData)
    }

    var itemClickListener: OnItemClickListener?= null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemName = itemView.findViewById<TextView>(R.id.itemName)
        val itemAmt = itemView.findViewById<TextView>(R.id.itemamt)
        val itemExpdate = itemView.findViewById<TextView>(R.id.itemexpdate)
        init{}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = productList[position].pname
        holder.itemAmt.text = productList[position].pquantity.toString() + "개"
        holder.itemExpdate.text = "유통기한 : " + productList[position].expdate.toString()
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}