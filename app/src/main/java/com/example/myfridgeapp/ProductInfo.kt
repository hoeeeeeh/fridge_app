package com.example.myfridgeapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.myfridgeapp.databinding.ActivityProductInfoBinding

class ProductInfo : AppCompatActivity() {
    private lateinit var binding: ActivityProductInfoBinding
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){    // Barcode 성공적 인식
            val tmpData = it.data?.getStringExtra("productName")
            Toast.makeText(this, "Product Name: $tmpData", Toast.LENGTH_SHORT).show()
            binding.productNameEditText.setText(tmpData)

            // 바코드로 상품 이름을 받으면 수정 불가능!
            binding.productNameEditText.isFocusable = false
        }
        else if(it.resultCode == Activity.RESULT_CANCELED){ // Barcode 인식 X
            Toast.makeText(this, "텍스트 입력으로 전환", Toast.LENGTH_SHORT).show()

        }
    }
    private var fridgeListDB = FridgeListDB(this)
    private var myDBHelper:MyDBHelper = MyDBHelper(this)
    private var multipleTextWatcherFlag = arrayOf(false, false, false)

    // 냉장고 db 가져와서 넣어줄 예정
    lateinit var items : MutableList<FridgeData>
    lateinit var myAdapter:ArrayAdapter<String>
    lateinit var myAdapter2:ArrayAdapter<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDB()
        initLayout()

    }

    private fun initLayout() {
        var pName: String = ""    // 상품 이름
        var fFloor = 0            // 냉장고의 '층'
        var fName = ""
        var fId: Int = 0         // 냉장고 번호
        var pQuantity: Int = 0  // 상품 수량
        var expDate: Int = 0    // 유통 기한

        binding.apply {

            // 일단 비활성화
            okButton.isEnabled = false

            // 바코드 or txt
            insertButton.setOnClickListener {
                callInputTypeAlertDlg()
            }


            // 상품 이름 입력
            productNameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().isNotEmpty()) {
                        multipleTextWatcherFlag[0] = true
                    }

                    // 전부 다 true -> is_enable
                    if (multipleTextWatcherFlag[0] && multipleTextWatcherFlag[1] && multipleTextWatcherFlag[2]) {
                        okButton.isEnabled = true
                    }
                }

            })

            // 상품 수량 입력
            quantityEditText.addTextChangedListener(object : TextWatcher {
//                var result: String = ""
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                    result = getString(R.string.input_lesson_quantity, p0.toString())
//                    expEditText.setText(result)
                }

                override fun afterTextChanged(p0: Editable?) {


                    if (p0.toString().isNotEmpty()) {
                        multipleTextWatcherFlag[1] = true
                    }

                    if (multipleTextWatcherFlag[0] && multipleTextWatcherFlag[1] && multipleTextWatcherFlag[2]) {
                        okButton.isEnabled = true
                    }

                }

            })

            // 상품 유통기한 입력
            expEditText.addTextChangedListener(object : TextWatcher {
//                var result: String = ""
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    // 단위 설정
//                    result = getString(R.string.input_lesson_date, p0.toString())
//                    expEditText.setText(result)

                    if (p0.toString().isNotEmpty()) {
                        multipleTextWatcherFlag[2] = true
                    }

                    if (multipleTextWatcherFlag[0] && multipleTextWatcherFlag[1] && multipleTextWatcherFlag[2]) {
                        okButton.isEnabled = true
                    }

                }

            })

            myAdapter = ArrayAdapter<String>(this@ProductInfo, android.R.layout.simple_spinner_dropdown_item)
            myAdapter2 = ArrayAdapter<Int>(this@ProductInfo, android.R.layout.simple_spinner_dropdown_item)


            // 냉장고 db에 대한 정보를 가져오기.
            for(i:Int in 0 until items.size) {
                myAdapter.add(items[i].name)
            }

            for(i:Int in 0 until items.size){
                myAdapter2.add(items[i].fid)
            }

            spinner.adapter = myAdapter
            spinner2.adapter = myAdapter2

            // 냉장고 spinner (name)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    // spinner 의 item 이 선택되었을 때, 해당 position 에 해당하는 name, pid 가져와야 함.
                    fName = items[position].name

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // 0번째 가져오면 안되나?
                }

            }

            // 냉장고 spinner (floor)
            spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    fFloor = items[position].floor
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

            okButton.setOnClickListener {
                val product = Product(0, fName, fFloor, pName, pQuantity, expDate)
                if(myDBHelper.insertProduct(product)){
                    Log.i("DB", "insert success")
                }
                else{
                    Log.i("DB", "insert failed")
                }

            }

        }
    }


    private fun initDB(){
        items = fridgeListDB.getFridgeList()

        val dbFile = getDatabasePath("fridgedb.db")
        if(!dbFile.parentFile.exists()){
            dbFile.mkdir()
        }

        if(!dbFile.exists()){
            Log.d("testlog", "냉장고 DB 없음.")
        }

    }

    private fun callInputTypeAlertDlg(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("바코드로 입력하시겠습니까?")
            .setTitle("바코드 촬영")
            .setPositiveButton("OK"){
                _, _ ->
                // 바코드 인식 화면으로 보냄냄
                var intent = Intent(this, BarcodeScan::class.java)
                activityResultLauncher.launch(intent)
            }
            .setNegativeButton("CANCEL"){
                _, _ ->

            }
        val dlg = builder.create()
        dlg.show()
    }




}