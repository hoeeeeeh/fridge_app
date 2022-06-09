package com.example.myfridgeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class MyDBHelper(val context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object{
        const val DB_NAME = "fridgedb.db"
        const val DB_VERSION = 1
        const val TABLE_NAME = "products"
        const val PID = "pid"
        const val FID = "fid"
        const val FNAME = "fname"
        const val FFLOOR = "ffloor"
        const val PNAME = "pname"
        const val PQUANTITY = "pquantity"
        const val EXPDATE = "expdate"
    }

    // Table 생성
    override fun onCreate(p0: SQLiteDatabase?) {
        val createTable = "create table if not exists $TABLE_NAME(" +
                "$PID integer primary key autoincrement, " +
//                "$FID integer, " +
                "$FNAME text," +
                "$FFLOOR text, " +
                "$PNAME text, " +
                "$PQUANTITY integer, " +
                "$EXPDATE integer);"
        p0!!.execSQL(createTable)
    }

    // db version 변경 시, 근데이거 무슨 뜻일까
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropTable = "drop table if exists $TABLE_NAME"

        p0!!.execSQL(dropTable)
        onCreate(p0)
    }

    // product 객체를 생성해서 넘겨주면 db에 data 삽입
    fun insertProduct(product: Product): Boolean {
        val value = ContentValues()
        value.put(FNAME, product.fName)
        value.put(FFLOOR, product.fFloor)
        value.put(PNAME, product.pname)
        value.put(PQUANTITY, product.pquantity)
        value.put(EXPDATE, product.expdate)

        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, value) > 0

        db.close()
        return flag

    }

    fun getAllProduct(): MutableList<Product>{
        val productList = mutableListOf<Product>()
        var strsql = "select * from $TABLE_NAME order by $PID asc;"

        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0

        cursor.moveToFirst()

        Log.d("name1", "cursorCount : ${cursor.count}")

        val columncount = cursor.columnCount
        Log.d("name1", "fridgeCount : $columncount")

        do{
            val columnPID = cursor.getColumnIndexOrThrow(MyDBHelper.PID)
            val columnFNAME = cursor.getColumnIndexOrThrow(MyDBHelper.FNAME)
            val columnFFLOOR = cursor.getColumnIndexOrThrow(MyDBHelper.FFLOOR)
            val columnPNAME = cursor.getColumnIndexOrThrow(MyDBHelper.PNAME)
            val columnFQUANTITY = cursor.getColumnIndexOrThrow(MyDBHelper.PQUANTITY)
            val columnEXPDATE = cursor.getColumnIndexOrThrow(MyDBHelper.EXPDATE)

            val product = Product(
                cursor.getInt(columnPID),
                cursor.getString(columnFNAME),
                cursor.getInt(columnFFLOOR),
                cursor.getString(columnPNAME),
                cursor.getInt(columnFQUANTITY),
                cursor.getInt(columnEXPDATE)
            )
            productList.add(product)
        }while(cursor.moveToNext())
        cursor.close()
        db.close()
        return productList
    }

    fun deleteProduct(product: Product) : Boolean{
        val strsql = "select * from $TABLE_NAME where $PID='${product.pid}';" // select * from product where pname = 'pname'
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME, "$PID=?", arrayOf(product.pid.toString()))
        }
        cursor.close()
        db.close()
        return flag
    }
    fun getProductRealTime(fName : String, pName : String, findFloor : Int): ArrayList<Product>{
        val productList = ArrayList<Product>()
        var strsql = "select * from $TABLE_NAME where ($FNAME = '$fName' AND $FFLOOR = '$findFloor' AND $PNAME like '%$pName%') order by $PID asc;"

        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if(!flag){
            return productList
        }

        cursor.moveToFirst()

        Log.d("name1", "cursorCount : ${cursor.count}")

        val columncount = cursor.columnCount
        Log.d("name1", "fridgeCount : $columncount")

        do{
            val columnPID = cursor.getColumnIndexOrThrow(MyDBHelper.PID)
            val columnFNAME = cursor.getColumnIndexOrThrow(MyDBHelper.FNAME)
            val columnFFLOOR = cursor.getColumnIndexOrThrow(MyDBHelper.FFLOOR)
            val columnPNAME = cursor.getColumnIndexOrThrow(MyDBHelper.PNAME)
            val columnFQUANTITY = cursor.getColumnIndexOrThrow(MyDBHelper.PQUANTITY)
            val columnEXPDATE = cursor.getColumnIndexOrThrow(MyDBHelper.EXPDATE)

            val product = Product(
                cursor.getInt(columnPID),
                cursor.getString(columnFNAME),
                cursor.getInt(columnFFLOOR),
                cursor.getString(columnPNAME),
                cursor.getInt(columnFQUANTITY),
                cursor.getInt(columnEXPDATE)
            )
            productList.add(product)
        }while(cursor.moveToNext())
        cursor.close()
        db.close()
        return productList
    }

    fun getProduct(findName : String, findFloor : Int): ArrayList<Product>{
        val productList = ArrayList<Product>()
        var strsql = "select * from $TABLE_NAME where ($FNAME = '$findName' AND $FFLOOR = '$findFloor') order by $PID asc;"

        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if(!flag){
            return productList
        }

        cursor.moveToFirst()

        Log.d("name1", "cursorCount : ${cursor.count}")

        val columncount = cursor.columnCount
        Log.d("name1", "fridgeCount : $columncount")

        do{
            val columnPID = cursor.getColumnIndexOrThrow(MyDBHelper.PID)
            val columnFNAME = cursor.getColumnIndexOrThrow(MyDBHelper.FNAME)
            val columnFFLOOR = cursor.getColumnIndexOrThrow(MyDBHelper.FFLOOR)
            val columnPNAME = cursor.getColumnIndexOrThrow(MyDBHelper.PNAME)
            val columnFQUANTITY = cursor.getColumnIndexOrThrow(MyDBHelper.PQUANTITY)
            val columnEXPDATE = cursor.getColumnIndexOrThrow(MyDBHelper.EXPDATE)

            val product = Product(
                cursor.getInt(columnPID),
                cursor.getString(columnFNAME),
                cursor.getInt(columnFFLOOR),
                cursor.getString(columnPNAME),
                cursor.getInt(columnFQUANTITY),
                cursor.getInt(columnEXPDATE)
            )
            productList.add(product)
        }while(cursor.moveToNext())
        cursor.close()
        db.close()
        return productList
    }
}