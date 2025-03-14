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
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridgeapp.databinding.ActivityItemListBinding
import com.example.myfridgeapp.databinding.FridgeColumnBinding
import com.example.myfridgeapp.databinding.ItemRowBinding
import com.example.refrigerator_manage.CartData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ItemList : AppCompatActivity() {
    private lateinit var productList : ArrayList<Product>
    private var selectedFloor : Int = -1
    lateinit var fridgeInsideDB : MyDBHelper
    lateinit var fridge : FridgeData
    lateinit var binding : ActivityItemListBinding
    lateinit var adapter : SelectedFloorAdapter
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
        adapter = SelectedFloorAdapter(productList)

        binding.apply{
            floorItemList.text = "${fridge.name} ${selectedFloor}층"
            productListRecyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
            productListRecyclerView.adapter = adapter
            searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextChange(qString: String): Boolean {
                    val productListTemp = fridgeInsideDB.getProductRealTime(fridge.name, qString, selectedFloor)
                    productList.clear()
                    for(i in productListTemp){
                        productList.add(i)
                    }
                    adapter.notifyDataSetChanged()
                    return true
                }
                override fun onQueryTextSubmit(qString: String): Boolean {
                    return true
                }
            })
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
                    notifyItemRangeChanged(position, getItemCount() - position);
                }
            }
            Log.d("testtest", "${position}")
            dlg.show(productList[position])
        }
        holder.itemName.text = "상품명: " + productList[position].pname
        holder.itemAmt.text = "수량: " + productList[position].pquantity.toString() + "개"
        val dateNow = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val expDate: LocalDate = LocalDate.parse(productList[position].expdate.toString(), formatter)
        var expDay = ""
        val cmpDate = expDate.compareTo(dateNow)
        if(cmpDate > 0){
            expDay = "남은 유통기한 : ${ChronoUnit.DAYS.between(dateNow,expDate)}일"
        }else if(cmpDate == 0){
            expDay = "유통기한이 오늘까지 입니다!"
        }else{
            expDay = "유통기한이 지났습니다!"
        }


        holder.itemExpdate.text = expDay
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}