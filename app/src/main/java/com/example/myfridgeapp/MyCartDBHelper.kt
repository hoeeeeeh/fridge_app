package com.example.refrigerator_manage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myfridgeapp.R
import com.example.myfridgeapp.cart_view
import java.io.FileOutputStream

class MyCartDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_NAME = "cart.db"
        val DB_VERSION = 1
        val TABLE_NAME = "Item_Cart"
        val PID = "pid"
        val PNAME = "pname"
        val PQUANTITY = "pquantity"
    }

    fun savetorecycler() {
        val strsql = "select * from $TABLE_NAME"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(!cursor.moveToFirst()){
            return
        }
        val attrcount = cursor.columnCount
        val activity = context as cart_view // 메인 액티비티 요소 접근 가능
        do {
            var data = CartData(0,"",0)
            for(i in 0 until attrcount){
                var pid = cursor.getString(0).toInt()
                var pname = cursor.getString(1)
                var pquantity = cursor.getString(2).toInt()
                data.pid = pid
                data.nameMenu = pname
                data.menuCnt = pquantity
           }
            activity.data.add(data)
        } while (cursor.moveToNext())
        cursor.close()
        db.close()
    }


    fun insertProduct(product: CartData): Boolean {
        val values = ContentValues()
        values.put(PNAME, product.nameMenu)
        values.put(PQUANTITY, product.menuCnt)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values) > 0
        db.close()
        return flag

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME(" +
                "$PID integer primary key autoincrement, " +
                "$PNAME text, " +
                "$PQUANTITY integer);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    //select * from product where pid = 'pid';
    fun deleteProduct(pid: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $PID = '$pid';"
        val db =writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME,"$PID=?", arrayOf(pid))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun updateProduct(product: CartData): Boolean {
        val pid = product.pid
        val strsql = "select * from $TABLE_NAME where $PID = '$pid';"
        val db =writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        if(flag){
            cursor.moveToFirst()
            val values = ContentValues()
            values.put(PNAME, product.nameMenu)
            values.put(PQUANTITY, product.menuCnt)
            db.update(TABLE_NAME,values,"$PID=?", arrayOf(pid.toString()))
        }
        cursor.close()
        db.close()
        return flag
    }

}