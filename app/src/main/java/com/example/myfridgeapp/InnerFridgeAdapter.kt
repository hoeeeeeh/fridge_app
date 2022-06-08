package com.example.myfridgeapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridgeapp.InnerMostFridgeAdapter
import com.example.myfridgeapp.R
import com.example.myfridgeapp.databinding.FridgeRowBinding


class InnerFridgeAdapter(private val fridge : FridgeData) : RecyclerView.Adapter<InnerFridgeAdapter.ViewHolder>(){
    interface OnItemClickListener{
        // fun OnItemClick(data: MyData)
    }

    var itemClickListener: OnItemClickListener ?= null
    private lateinit var fridgeInsideDB : MyDBHelper
    private lateinit var productList : List<Product>



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val innermostFridge = itemView.findViewById<RecyclerView>(R.id.innerRecyclerView)
        val floorNum = itemView.findViewById<TextView>(R.id.floorNum)
        init{
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FridgeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.floorNum.text = "${position+1} 층"
        holder.floorNum.setOnClickListener {
            val intent = Intent(holder.floorNum.context, ItemList::class.java)
            intent.putExtra("fridge", fridge)
            intent.putExtra("selectedFloor", position + 1)
            holder.floorNum.context.startActivity(intent)
        }
        holder.innermostFridge.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        fridgeInsideDB = MyDBHelper(holder.floorNum.context)
        productList = fridgeInsideDB.getProduct(fridge.name, position + 1)

        holder.innermostFridge.adapter = InnerMostFridgeAdapter(productList)
        holder.innermostFridge.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener{
            // 중첩 리사이클러뷰에서 부모(혹은 부모보다 위)와 스크롤 방향이 겹칠 때, 자식 우선으로 수행
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val action = e.action
                when (action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getItemCount(): Int {
        return fridge.floor
    }
}