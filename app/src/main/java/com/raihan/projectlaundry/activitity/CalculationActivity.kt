package com.raihan.projectlaundry.activitity

import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.adapter.GridCalculationAdapter
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ItemModel
import com.raihan.projectlaundry.model.ServiceModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CalculationActivity : AppCompatActivity(), GridCalculationAdapter.GridAdapterCallback {

    private lateinit var service: TextView
    private lateinit var txtTotalUang: TextView
    private lateinit var desc:TextView
    private lateinit var btnNext: Button
    var totalBerat: Double = 0.0
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    private lateinit var gridList: List<ItemModel>
    private lateinit var gridAdapter: GridCalculationAdapter
    private lateinit var grid: GridView
    val apiService = ApiClient.apiService
    lateinit var serviceList:List<ServiceModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculation)

        service = findViewById(R.id.txtTitleCalc)
        desc = findViewById(R.id.txtDescCalc)
        grid = findViewById(R.id.gridItem)

        val actionbar = supportActionBar
        actionbar?.title = "Perhitungan Barang Cucian"

        val bundle: Bundle? = intent.extras

        txtTotalUang = findViewById(R.id.txtTotalUang)
        btnNext = findViewById(R.id.btnNext)


        gridList = ArrayList()

        txtTotalUang.text = "0.0 Kg\t" + currencyFormat.format(0L)

        if (bundle != null) {
            service.text = bundle.getString("service")
            val service_id = bundle.getString("service_id")
            if (service_id != null) {
                getService(service_id)
                getAllItems(service_id)

            } else {
                finish()
            }
        }

        btnNext.setOnClickListener {
//            val intent = Intent(this,ConfirmActivity::class.java)
//            startActivity(intent)
            gridAdapter.Result()

        }
    }

    fun getService(service_id: String){
        apiService.getService(service_id).enqueue(object :Callback<List<ServiceModel>>{
            override fun onResponse(
                call: Call<List<ServiceModel>>,
                response: Response<List<ServiceModel>>
            ) {
                if (response.isSuccessful){
                    serviceList = response.body()!!
                    val message = "Berhasil"
                    showMessage(message)
                    serviceList.forEach {
                        desc.text = "${it.desc}\n"
                    }
                }else{
                    val message = "Error 1: ${response.body()}"
                    showMessage(message)
                }
            }

            override fun onFailure(call: Call<List<ServiceModel>>, t: Throwable) {
                val message = "Error 2: $t"
                showMessage(message)
            }

        })
    }

    fun getAllItems(service_id:String){
        apiService.getAllItemsService(service_id).enqueue(object :Callback<List<ItemModel>> {
            override fun onResponse(
                call: Call<List<ItemModel>>,
                response: Response<List<ItemModel>>
            ) {
                if (response.isSuccessful){
                    val items = response.body()!!
                    val message = "Berhasil"
                    showMessage(message)
                    items.let {
                        gridList = it

                        gridAdapter = GridCalculationAdapter(gridList,this@CalculationActivity,this@CalculationActivity)
                        grid.adapter = gridAdapter
                    }
                }else{
                    val message = "Error 3: ${response.body()}"
                    showMessage(message)
                }
            }

            override fun onFailure(call: Call<List<ItemModel>>, t: Throwable) {
                val message = "Error 4: $t"
                showMessage(message)
            }

        })
    }

    fun hitungTotal(berat: Double, operasi: Int) {
        val totalUang: Long
        if (operasi == 1) {
            totalBerat += berat
            totalUang = (((totalBerat.toInt() / 4) + 1) * 12000).toLong()
            txtTotalUang.text =
                "${String.format("%.2f", totalBerat)} Kg\t" + currencyFormat.format(totalUang)
        } else {
//            totalBerat = totalBerat - berat
            if (totalBerat - berat > 0.0) {
                totalBerat -= berat
                totalUang = (((totalBerat.toInt() / 4) + 1) * 12000).toLong()
                txtTotalUang.text =
                    "${String.format("%.2f", totalBerat)} Kg\t" + currencyFormat.format(totalUang)
            } else {
                txtTotalUang.text = "0.0 Kg\t" + currencyFormat.format(0L)
            }
        }
    }

    fun showMessage(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteService(service_id: String) {
        TODO("Not yet implemented")
    }

    override fun onUpdateService(service_id: String) {
        TODO("Not yet implemented")
    }

    override fun showResult(message: String) {
        showMessage(message)
    }


}