package com.example.myfridgeapp

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.refrigerator_manage.CartData


class popupDialog(context : Context)  {
    private val context_ = context
    private val dlg = Dialog(context)

    private lateinit var addCartBtn : Button
    private lateinit var deleteProductBtn : Button
    private lateinit var productInfo : TextView
    private lateinit var listener : MyDialogClickedListener

    private lateinit var fridgeInsideDB : MyDBHelper
    private lateinit var cartDBHelper : MyCartDBHelper

    fun show(product: Product){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.activity_popup_dialog)
        dlg.setCancelable(true)

        productInfo = dlg.findViewById(R.id.productInfo)
        productInfo.text = "${product.pname} 을 어떻게 할까요?"

        addCartBtn = dlg.findViewById(R.id.addcartbtn)
        addCartBtn.setOnClickListener {
            cartDBHelper = MyCartDBHelper(context_)
            cartDBHelper.insertProduct(CartData(product.pid, product.pname, product.pquantity))
            addToCart()
            dlg.dismiss()
        }

        deleteProductBtn = dlg.findViewById(R.id.deleteproductbtn)
        deleteProductBtn.setOnClickListener {
            fridgeInsideDB = MyDBHelper(context_)
            fridgeInsideDB.deleteProduct(product)
            deleteProduct(product)
            listener.onDeleteClicked(true)
            dlg.dismiss()
        }

        dlg.show()
    }




    private fun deleteProduct(product: Product) {

    }

    private fun addToCart(){

    }

    fun setOnDeleteClickedListener(listener: (Boolean) -> Unit) {
        this.listener = object: MyDialogClickedListener {
            override fun onDeleteClicked(flag: Boolean) {
                listener(flag)
            }
        }
    }

    interface MyDialogClickedListener{
        fun onDeleteClicked(flag: Boolean)
    }
}