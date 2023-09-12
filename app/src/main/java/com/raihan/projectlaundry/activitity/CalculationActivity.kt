package com.raihan.projectlaundry.activitity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
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
import kotlin.math.ceil
import kotlin.random.Random

class CalculationActivity : AppCompatActivity(), GridCalculationAdapter.GridAdapterCallback {

    private lateinit var service: TextView
    private lateinit var txtTotalUang: TextView
    private lateinit var desc:TextView
    private lateinit var btnNext: Button
    private lateinit var chkPickUp: CheckBox
    private lateinit var chkDropOff:CheckBox
    var itemSum = mutableMapOf<String,Int>()
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    private var gridList: List<ItemModel>? = null
    private lateinit var gridAdapter: GridCalculationAdapter
    private lateinit var grid: GridView
    val apiService = ApiClient.apiService
    var serviceList:List<ServiceModel>? = null
    var totalWeight:Float = 0.0f
    val orderId = "order" + generateRandomNumbers()
    var perKg: Float = 0.0f
    var price: Int = 0
    var afterPrice: Long = 0L
    var pickUp:Int = 0
    var dropOff:Int = 0
    var totalPriceWeight:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculation)

        service = findViewById(R.id.txtTitleCalc)
        desc = findViewById(R.id.txtDescCalc)
        grid = findViewById(R.id.gridItem)
        chkDropOff = findViewById(R.id.chkDropOff)
        chkPickUp = findViewById(R.id.chkPickUp)

        val actionbar = supportActionBar
        actionbar?.title = "Perhitungan Barang Cucian"

        val bundle: Bundle? = intent.extras

        txtTotalUang = findViewById(R.id.txtTotalUang)
        btnNext = findViewById(R.id.btnNext)


        gridList = ArrayList()

        txtTotalUang.setText("0.0 Kg/t" + currencyFormat.format(0L))

        if (bundle != null) {
            service.text = bundle.getString("service")
            val service_id = bundle.getString("service_id")
//            val username = bundle.getString("username")
//            showMessage(username.toString())
            if (service_id != null) {
                getService(service_id)
                getAllItems(service_id)
            }else{
                finish()
            }
        }

        chkPickUp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                pickUp += 5000
                total()
            }else {
                pickUp -= 5000
                total()
            }
        }

        chkDropOff.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                dropOff += 5000
                total()
            }else {
                dropOff -= 5000
                total()
            }
        }

        btnNext.setOnClickListener {
            if (itemSum.isNotEmpty()) {
                val confirm = Intent(this, ConfirmActivity::class.java)
                val sent = Bundle()
                sent.putString("username", bundle!!.getString("username"))
                sent.putString("phone_number", bundle!!.getString("phone_number"))
                sent.putString("address", bundle!!.getString("address"))
                sent.putString("order_id", orderId)
                sent.putFloat("total_weight", totalWeight)
                sent.putLong("after_price", afterPrice)
                if (chkDropOff.isChecked) {
                    sent.putString("drop_off_opt", "yes")
                } else {
                    sent.putString("drop_off_opt", "no")
                }
                if (chkPickUp.isChecked) {
                    sent.putString("pick_up_opt", "yes")
                } else {
                    sent.putString("pick_up_opt", "no")
                }
                sent.putInt("drop_off", dropOff)
                sent.putInt("pick_up", pickUp)
                sent.putInt("total_price_weight", totalPriceWeight)
                val hashItem = HashMap(itemSum)
                sent.putSerializable("hashItem", hashItem)
                sent.putString("service_name", serviceList!![0].name)
                sent.putInt("service_price",serviceList!![0].price)
                sent.putInt("service_per_kg",serviceList!![0].per_kg)
                sent.putString("service_id",serviceList!![0].service_id)
                confirm.putExtras(sent)
                startActivity(confirm)
            }else{
                val message = "Harap isi barang cucian"
                showMessage(message)
            }

//            showMessage(orderId+"\n"+itemSum.toString()+"\nBerat: ~"+totalWeight+"Kg\nRp."+afterPrice+"\n"+perKg)
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
//                    val message = "Berhasil"
//                    showMessage(message)
                    serviceList!!.forEach {
                        desc.text = "${it.desc}\n"
                        perKg = it.per_kg.toFloat()
                        price = it.price
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
//                    val message = "Berhasil"
//                    showMessage(message)
                    items.let {
                        gridList = it

                        gridAdapter = GridCalculationAdapter(gridList!!,this@CalculationActivity,this@CalculationActivity)
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

    fun showMessage(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun getTotalWeight(sumWeight:Float) {
        totalWeight = sumWeight
    }

    override fun getTotalItem(itemId:String, sum:Int) {
        itemSum[itemId] = sum
    }

    override fun getItemDesc(message: String) {
        showMessage(message)
    }

    override fun total() {
        totalPriceWeight = ((ceil((totalWeight/perKg))) * price).toInt()
        afterPrice = (totalPriceWeight + pickUp + dropOff).toLong()
        val text = "${totalWeight} Kg\t${currencyFormat.format((afterPrice))}"
        txtTotalUang.text = text
    }
    fun generateRandomNumbers(): String {
        val random = Random(System.currentTimeMillis())
        var result = ""

        repeat(3) {
            val randomNumber = random.nextInt(10)
            result = result + randomNumber
        }

        return result
    }


}