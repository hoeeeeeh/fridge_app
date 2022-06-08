package com.example.myfridgeapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridgeapp.databinding.InnerfridgeRowBinding

class InnerMostFridgeAdapter(val productList : List<Product>) : RecyclerView.Adapter<InnerMostFridgeAdapter.ViewHolder>(){
    interface OnItemClickListener{
        // fun OnItemClick(data: MyData)
    }
    var itemClickListener: OnItemClickListener?= null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val productName = itemView.findViewById<TextView>(R.id.productName)
        init{}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = InnerfridgeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.productName.text = productList[position].pname
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}