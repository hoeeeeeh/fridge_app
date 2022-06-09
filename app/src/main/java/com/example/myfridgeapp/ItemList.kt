package com.example.myfridgeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridgeapp.databinding.ActivityItemListBinding
import com.example.myfridgeapp.databinding.FridgeColumnBinding
import com.example.myfridgeapp.databinding.ItemRowBinding
import com.example.refrigerator_manage.CartData

class ItemList : AppCompatActivity() {
    private lateinit var productList : ArrayList<Product>
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

class SelectedFloorAdapter(val productList: ArrayList<Product>) : RecyclerView.Adapter<SelectedFloorAdapter.ViewHolder>(){

    interface OnItemClickListener{
         fun OnItemClick(data: Product, pos:Int)
    }

    var itemClickListener: OnItemClickListener?= null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val product = itemView.findViewById<LinearLayout>(R.id.productLayout)
        val itemName = itemView.findViewById<TextView>(R.id.itemName)
        val itemAmt = itemView.findViewById<TextView>(R.id.itemamt)
        val itemExpdate = itemView.findViewById<TextView>(R.id.itemexpdate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.product.setOnClickListener {
            val dlg = popupDialog(holder.product.context)
            dlg.setOnDeleteClickedListener {
                if(it){
                    productList.removeAt(position)
                    this.notifyItemRemoved(position)
                }
            }
            dlg.show(productList[position])
        }
        holder.itemName.text = "상품명: " + productList[position].pname
        holder.itemAmt.text = "수량: " + productList[position].pquantity.toString() + "개"
        holder.itemExpdate.text = "남은 유통기한 : " + productList[position].expdate.toString() +"일"
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}