package com.raihan.projectlaundry.activitity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.activitity.account.AccountActivity
import com.raihan.projectlaundry.activitity.item.ItemActivity
import com.raihan.projectlaundry.activitity.service.ServiceActivity
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.SalesToday
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class AdminActivity : AppCompatActivity() {
    val apiService = ApiClient.apiService
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    lateinit var txtSalesTotal :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val salesPage = findViewById<Button>(R.id.btnDetail)
        txtSalesTotal = findViewById(R.id.txtTotalPenjualan)
        val servicePage = findViewById<Button>(R.id.btnService)
        val itemPage = findViewById<Button>(R.id.btnItem)
        val accountPage = findViewById<Button>(R.id.btnAccount)
        val historyPage = findViewById<Button>(R.id.btnHistory)

        val actionBar = supportActionBar
        actionBar?.title = "Halaman Admin"

        salesPage.setOnClickListener {
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)
        }

        servicePage.setOnClickListener {
            val intent = Intent(this, ServiceActivity::class.java)
            startActivity(intent)
        }
        itemPage.setOnClickListener {
            val intent = Intent(this, ItemActivity::class.java)
            startActivity(intent)
        }
        accountPage.setOnClickListener {
            val account = Intent(this, AccountActivity::class.java)
            startActivity(account)
        }
        historyPage.setOnClickListener {
//            val intent = Intent(this, ServiceActivity::class.java)
//            startActivity(intent)
        }
    }

    fun getSalesToday(){
        apiService.getTodaySale().enqueue(object :Callback<List<SalesToday>>{
            override fun onResponse(
                call: Call<List<SalesToday>>,
                response: Response<List<SalesToday>>
            ) {
                if(response.isSuccessful){
                    val sales = response.body()!!
                    sales.forEach {
                        "Rp." + currencyFormat.format(it.price.toLong())
                    }
                }else{
                    val message = "Error : ${response.body()}"
                    showMessage(message)
                    "Rp." + currencyFormat.format(0L)
                }
            }

            override fun onFailure(call: Call<List<SalesToday>>, t: Throwable) {
                val message = "Error : $t"
                showMessage(message)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun logout(){
        // Hapus data sesi atau lakukan operasi logout lainnya di sini

        // Contoh: Menghapus data sesi
        val sharedPref = getSharedPreferences("session", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        // Buka halaman login
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings ->{

            true
        }
        R.id.action_logout ->{
            logout()
            true
        }
        R.id.action_coba2 ->{
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun showMessage(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}