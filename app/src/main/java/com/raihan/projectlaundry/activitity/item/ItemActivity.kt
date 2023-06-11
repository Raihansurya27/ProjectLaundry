package com.raihan.projectlaundry.activitity.item

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
import com.raihan.projectlaundry.adapter.GridItemAdapter
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.ItemModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemActivity : AppCompatActivity(), GridItemAdapter.GridAdapterCallback {
    private lateinit var gridList: List<ItemModel>
    private lateinit var gridAdapter: GridItemAdapter
    private lateinit var btnMake: Button
    val apiService = ApiClient.apiService
    private lateinit var progBar: ProgressBar
    private lateinit var search: EditText
    private lateinit var grid: GridView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        progBar = findViewById(R.id.progBar)
        btnMake = findViewById(R.id.btnMakeItem)
        search = findViewById(R.id.editSearch)
        grid = findViewById(R.id.gridItem)
        val actionBar = supportActionBar
        actionBar?.title = "Pengaturan Barang Cucian"

        gridList = ArrayList<ItemModel>()
        searchFromApi("")
        search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

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
            val Newitem = Intent(this, NewItemActivity::class.java)
            startActivity(Newitem)
        }
    }

    private fun searchFromApi(keyword: String){
        val getAll = apiService.getAllItem()
        val searchItem = apiService.searchItem(keyword)

        if(keyword.isNotBlank()){
            searchItem.enqueue(object: Callback<List<ItemModel>> {
                override fun onResponse(
                    call: Call<List<ItemModel>>,
                    response: Response<List<ItemModel>>
                ) {
                    if (response.isSuccessful) {
                        val items = response.body()

                        progBar.visibility = View.GONE

                        items!!.let {
                            gridList = it

                            // Inisialisasi adapter dan tampilkan data di GridView
                            gridAdapter = GridItemAdapter(gridList, this@ItemActivity, this@ItemActivity)
                            grid.adapter = gridAdapter

                        }

                    }
                }

                override fun onFailure(call: Call<List<ItemModel>>, t: Throwable) {
                    var message = "Gagal 2 \n$t"
                    showMessage(message)
                }

            })
        }else{
            getAll.enqueue(object : Callback<List<ItemModel>> {
                override fun onResponse(
                    call: Call<List<ItemModel>>,
                    response: Response<List<ItemModel>>
                ) {
                    if (response.isSuccessful) {
                        val items = response.body()

                        progBar.visibility = View.GONE

                        items!!.let {
                            gridList = it

                            // Inisialisasi adapter dan tampilkan data di GridView
                            gridAdapter = GridItemAdapter(gridList, this@ItemActivity,this@ItemActivity)
                            grid.adapter = gridAdapter

                        }

                    }
                }

                override fun onFailure(call: Call<List<ItemModel>>, t: Throwable) {
                    var message = "Gagal 2 \n$t"
                    showMessage(message)
                }

            })
        }
    }

    override fun onDeleteItem(itemId: String) {
        progBar.visibility = View.VISIBLE
        apiService.deleteItem(itemId).enqueue(object :Callback<ApiResponse>{
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful){
                    progBar.visibility = View.GONE
                    val apiResponse = response.body()
                    var message = "${apiResponse!!.message}"
                    showMessage(message)
                    searchFromApi("")
                }else{
                    progBar.visibility = View.GONE
                    var message = "Gagal 1 "+response.body() + " "+response.body()
                    showMessage(message)
                    searchFromApi("")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                progBar.visibility = View.GONE
                var message = "Gagal 2 + $t"
                showMessage(message)
                searchFromApi("")
            }

        })
    }

    override fun onUpdateItem(itemId: String) {
        val update = Intent(this, UpdateItemActivity::class.java)
        val bundle = Bundle()
        bundle.putString("item_id",itemId)
        update.putExtras(bundle)
        startActivity(update)
    }

    fun showMessage(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}