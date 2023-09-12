package com.raihan.projectlaundry.activitity.history

import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.adapter.GridHistoryAdapter
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.HistoryModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var gridList1: List<HistoryModel>
//    private lateinit var gridList2: List<GridHistoryModel>
    private lateinit var gridAdapter: GridHistoryAdapter
    val apiService = ApiClient.apiService
    private lateinit var grid:GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        grid = findViewById(R.id.gridDoing)
        getAllDoingOrders()
        val actionBar = supportActionBar
        actionBar?.title = "Riwayat Cucian"
        grid.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val orderId = gridList1[position].orderId
            showMessage(orderId)
        }
    }

    fun getAllDoingOrders(){
        apiService.getAllDoingOrder().enqueue(object : Callback<List<HistoryModel>> {
            override fun onResponse(
                call: Call<List<HistoryModel>>,
                response: Response<List<HistoryModel>>
            ) {
                if (response.isSuccessful){
                    val items = response.body()!!
                    items.let {
                        gridList1 = it

                        gridAdapter = GridHistoryAdapter(gridList1, this@HistoryActivity)
                        grid.adapter = gridAdapter
                    }

                }
            }

            override fun onFailure(call: Call<List<HistoryModel>>, t: Throwable) {
                val message = "Error 2 : $t"
                showMessage(message)
            }

        })
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}