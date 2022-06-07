package com.example.myfridgeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class FridgeListDB(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "fridgelistdb.db"
        val DB_VERSION = 1
        val TABLE_NAME = "fridges"
        val FRIDGE_ID = "id"
        val FRIDGE_NAME ="name"
        val FRIDGE_FLOOR = "floor"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME("+
                "$FRIDGE_ID integer primary key autoincrement, "+
                "$FRIDGE_NAME text, "+
                "$FRIDGE_FLOOR integer);"
        db!!.execSQL(create_table)
    }

    fun insertFridge(fridge: FridgeData):Boolean{
        Log.d("name1", fridge.name)
        val values = ContentValues()
        values.put(FRIDGE_NAME,fridge.name)
        values.put(FRIDGE_FLOOR, fridge.floor)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values) > 0
        db.close()
        return flag
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun getFridgeList():MutableList<FridgeData>{
        val fridgeList = mutableListOf<FridgeData>()

        var strsql = "select $FRIDGE_NAME,$FRIDGE_FLOOR, $FRIDGE_ID from $TABLE_NAME order by id asc;"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0

        cursor.moveToFirst()

        Log.d("name1", "cursorCount : ${cursor.count}")

        val fridgecount = cursor.columnCount
        Log.d("name1", "fridgeCount : $fridgecount")

        do{
            val columnName = cursor.getColumnIndexOrThrow(FRIDGE_NAME)
            val columnFloor = cursor.getColumnIndexOrThrow(FRIDGE_FLOOR)
            val columnFID = cursor.getColumnIndexOrThrow(FRIDGE_ID)
            val fridge = FridgeData(cursor.getString(columnName), cursor.getInt(columnFloor),cursor.getInt(columnFID))
            fridgeList.add(fridge)
        }while(cursor.moveToNext())
        cursor.close()
        db.close()
        return fridgeList
    }
}