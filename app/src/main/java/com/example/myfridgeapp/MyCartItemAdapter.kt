package com.example.refrigerator_manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridgeapp.databinding.RowBinding

class MyCartItemAdapter(val items: ArrayList<CartData>) : RecyclerView.Adapter<MyCartItemAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun OnItemClick(data: CartData, pos: Int)
    }

    fun removeItem(pos: Int) {
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    var increaseLinstener: OnItemClickListener? = null
    var decreaseListener: OnItemClickListener? = null
    var hrefListener: OnItemClickListener? = null
    var deleteListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: RowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.increasebtn.setOnClickListener {
                increaseLinstener?.OnItemClick(items[adapterPosition], adapterPosition)

            }
            binding.decreasebtn.setOnClickListener {
                decreaseListener?.OnItemClick(items[adapterPosition], adapterPosition)
            }
            binding.itemName.setOnClickListener {
                hrefListener?.OnItemClick(items[adapterPosition], adapterPosition)
            }
            binding.deletebtn.setOnClickListener {
                deleteListener?.OnItemClick(items[adapterPosition],adapterPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            amt.text = items[position].menuCnt.toString()
            itemName.text = items[position].nameMenu
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}