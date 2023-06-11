package com.raihan.projectlaundry.activitity.account

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
import com.raihan.projectlaundry.adapter.GridUserAdapter
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity : AppCompatActivity(), GridUserAdapter.GridAdapterCallback {

    private lateinit var gridList: List<UserModel>
    private lateinit var gridAdapter: GridUserAdapter
    private lateinit var btnMake: Button
    val apiService = ApiClient.apiService
    private lateinit var progBar: ProgressBar
    private lateinit var search: EditText
    private lateinit var grid: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        grid = findViewById(R.id.gridAccount)
        btnMake = findViewById(R.id.btnMakeAccount)
        search = findViewById(R.id.editSearch)
        progBar = findViewById(R.id.progBar)
        search.clearFocus()
        val actionBar = supportActionBar
        actionBar?.title = "Pengaturan Akun"

        gridList = ArrayList()
        searchFromApi("")

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                progBar.visibility = View.VISIBLE
                searchFromApi(keyword)
            }

            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().trim()
                progBar.visibility = View.VISIBLE
                searchFromApi(keyword)
            }

        })

        progBar.visibility = View.GONE

        btnMake.setOnClickListener {
            progBar.visibility = View.VISIBLE
            val NewAccount = Intent(this, NewAccountActivity::class.java)
            startActivity(NewAccount)
        }

    }

    private fun searchFromApi(keyword: String) {
        val getAll = apiService.getAllUser()
        val searchUser = apiService.searchUser(keyword)

        if (keyword.isNotBlank()) {
            searchUser.enqueue(object : Callback<List<UserModel>> {
                override fun onResponse(
                    call: Call<List<UserModel>>,
                    response: Response<List<UserModel>>
                ) {
                    if (response.isSuccessful) {
                        val users = response.body()

                        progBar.visibility = View.GONE

                        users!!.let {
                            gridList = it

                            // Inisialisasi adapter dan tampilkan data di GridView
                            gridAdapter = GridUserAdapter(
                                gridList,
                                this@AccountActivity,
                                this@AccountActivity
                            )
                            grid.adapter = gridAdapter

                        }

                    }
                }

                override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                    var message = "Gagal 2 \n$t"
                    showMessage(message)
                }

            })
        } else {
            getAll.enqueue(object : Callback<List<UserModel>> {
                override fun onResponse(
                    call: Call<List<UserModel>>,
                    response: Response<List<UserModel>>
                ) {
                    if (response.isSuccessful) {
                        val users = response.body()

                        progBar.visibility = View.GONE

                        users!!.let {
                            gridList = it

                            // Inisialisasi adapter dan tampilkan data di GridView
                            gridAdapter = GridUserAdapter(
                                gridList,
                                this@AccountActivity,
                                this@AccountActivity
                            )
                            grid.adapter = gridAdapter

                        }

                    }
                }

                override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                    var message = "Gagal 2 \n$t"
                    showMessage(message)
                }

            })
        }
    }


    override fun onDeleteUser(phoneNumber: String) {
        progBar.visibility = View.VISIBLE
        apiService.deleteUser(phoneNumber).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    progBar.visibility = View.GONE
                    val apiResponse = response.body()
                    var message = "${apiResponse!!.message}"
                    showMessage(message)
                    searchFromApi("")
                } else {
                    progBar.visibility = View.GONE
                    var message = "Gagal 1 " + response.body() + " " + response.body()
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

    override fun onUpdateUser(phoneNumber: String) {
        val update = Intent(this, UpdateAccountActivity::class.java)
        val bundle = Bundle()
        bundle.putString("phone_number", phoneNumber)
        update.putExtras(bundle)
        startActivity(update)
//        Toast.makeText(this,phoneNumber,Toast.LENGTH_SHORT).show()
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}