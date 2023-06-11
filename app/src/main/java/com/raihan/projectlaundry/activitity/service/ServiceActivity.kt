package com.raihan.projectlaundry.activitity.service

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.adapter.GridServiceAdapter
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.ServiceModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceActivity : AppCompatActivity(), GridServiceAdapter.GridAdapterCallback{

    private lateinit var gridList: List<ServiceModel>
    private lateinit var gridAdapter: GridServiceAdapter
    private lateinit var btnMake: Button
    val apiService = ApiClient.apiService
    private lateinit var progBar: ProgressBar
    private lateinit var search: EditText
    private lateinit var grid: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        progBar = findViewById(R.id.progBar)
        btnMake = findViewById(R.id.btnMakeService)
        search = findViewById(R.id.editSearch)
        grid = findViewById(R.id.gridService)
        val actionBar = supportActionBar
        actionBar?.title = "Pengaturan Layanan"


        gridList = ArrayList()
        searchFromApi("")
        search.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                searchFromApi(keyword)
            }

            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().trim()
                searchFromApi(keyword)
            }

        })

        progBar.visibility = View.VISIBLE

        btnMake.setOnClickListener {
            val intent = Intent(this, NewServiceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun searchFromApi(keyword: String){
        val getAll = apiService.getAllService()
        val searchService = apiService.searchService(keyword)

        if(keyword.isNotBlank()){
            searchService.enqueue(object: Callback<List<ServiceModel>> {
                override fun onResponse(
                    call: Call<List<ServiceModel>>,
                    response: Response<List<ServiceModel>>
                ) {
                    if (response.isSuccessful) {
                        val items = response.body()

                        progBar.visibility = View.GONE

                        items!!.let {
                            gridList = it

                            // Inisialisasi adapter dan tampilkan data di GridView
                            gridAdapter = GridServiceAdapter(gridList, this@ServiceActivity, this@ServiceActivity)
                            grid.adapter = gridAdapter

                        }

                    }
                }

                override fun onFailure(call: Call<List<ServiceModel>>, t: Throwable) {
                    val message = "Gagal 2 \n$t"
                    showMessage(message)
                }

            })
        }else{
            getAll.enqueue(object : Callback<List<ServiceModel>> {
                override fun onResponse(
                    call: Call<List<ServiceModel>>,
                    response: Response<List<ServiceModel>>
                ) {
                    if (response.isSuccessful) {
                        val items = response.body()

                        progBar.visibility = View.GONE

                        items!!.let {
                            gridList = it

                            // Inisialisasi adapter dan tampilkan data di GridView
                            gridAdapter = GridServiceAdapter(gridList, this@ServiceActivity,this@ServiceActivity)
                            grid.adapter = gridAdapter

                        }

                    }
                }

                override fun onFailure(call: Call<List<ServiceModel>>, t: Throwable) {
                    val message = "Gagal 2 \n$t"
                    showMessage(message)
                }

            })
        }
    }

    override fun onDeleteService(service_id: String) {
        progBar.visibility = View.VISIBLE
        apiService.deleteService(service_id).enqueue(object :Callback<ApiResponse>{
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful){
                    progBar.visibility = View.GONE
                    val apiResponse = response.body()
                    val message = "hasil = ${apiResponse!!.message}"
                    showMessage(message)
                    searchFromApi("")
                }else{
                    progBar.visibility = View.GONE
                    val message = "Gagal 1 "+response.body() + " "+response.body()
                    showMessage(message)
                    searchFromApi("")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                progBar.visibility = View.GONE
                val message = "Gagal 2 + $t"
                showMessage(message)
                searchFromApi("")
            }

        })
    }

    override fun onUpdateService(service_id: String) {
        val update = Intent(this, UpdateServiceActivity::class.java)
        val bundle = Bundle()
        bundle.putString("service_id",service_id)
        update.putExtras(bundle)
        startActivity(update)
//        showMessage(service_id)
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}