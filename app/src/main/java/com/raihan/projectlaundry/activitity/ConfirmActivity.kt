package com.raihan.projectlaundry.activitity


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.ItemModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ConfirmActivity : AppCompatActivity() {

    private lateinit var inputEditText: TextView
    private lateinit var btnPay: Button
    private lateinit var txtTitle: TextView
    private lateinit var txtUserName: TextView
    private lateinit var txtPhoneNumber: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtOrder: TextView
    private lateinit var txtFinish: TextView
    private lateinit var txtItemName: TextView
    private lateinit var txtItemSum: TextView
    private lateinit var txtTotalWeight: TextView
    private lateinit var txtServiceName: TextView
    private lateinit var txtServicePrice: TextView
    private lateinit var txtSubTotal: TextView
    private lateinit var txtDeliver: TextView
    private lateinit var txtDeliverPrice: TextView
    private lateinit var txtDiscount: TextView
    private lateinit var txtPax: TextView
    private lateinit var txtTotalPrice: TextView
    private lateinit var progBar: ProgressBar

    var title = ""
    var username = ""
    var phoneNumber = ""
    var orderId = ""
    var address = ""
    var localtime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    var orderDate = localtime.format(formatter)
    var finishDate = localtime.plusDays(1).format(formatter)
    var resultCalculation = mutableMapOf<String, Int>()
    var totalWeight = 0.0f
    var serviceName = ""
    var servicePrice = 0
    var servicePerKg = 0
    var serviceId = ""
    var afterPrice = 0L
    var dropOffOpt = ""
    var pickUpOpt = ""
    var dropOff = 0
    var pickUp = 0
    var totalWeightPrice = 0
    var discount = 0
    val apiService = ApiClient.apiService
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    var name = ""
    var sum = ""
    lateinit var listItem: List<ItemModel>

//    val formattedNextDay = orderDate.format(formatter)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        val actionBar = supportActionBar
        actionBar?.title = "Konfirmasi Cucian"

        btnPay = findViewById(R.id.btnPay)
        txtTitle = findViewById(R.id.txtTitle)
        txtUserName = findViewById(R.id.textName)
        txtPhoneNumber = findViewById(R.id.textPhone)
        txtAddress = findViewById(R.id.textAddress)
        txtOrder = findViewById(R.id.textReceive)
        txtFinish = findViewById(R.id.textDone)
        txtItemName = findViewById(R.id.textCloth)
        txtItemSum = findViewById(R.id.textClothSum)
        txtTotalWeight = findViewById(R.id.textTotalClothSum)
        txtServiceName = findViewById(R.id.textService)
        txtServicePrice = findViewById(R.id.textServicePrice)
        txtSubTotal = findViewById(R.id.textSubtotalPrice)
        txtDeliver = findViewById(R.id.textDeliverService)
        txtDeliverPrice = findViewById(R.id.textDeliverServicePrice)
        txtDiscount = findViewById(R.id.textDiscountPrice)
        txtPax = findViewById(R.id.textPaxPrice)
        txtTotalPrice = findViewById(R.id.textTotalPrice)
        progBar = findViewById(R.id.progBar)

        progBar.visibility = View.VISIBLE

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            username = bundle.getString("username").toString()
            phoneNumber = bundle.getString("phone_number").toString()
            address = bundle.getString("address").toString()
            totalWeight = bundle.getFloat("total_weight")
            orderId = bundle.getString("order_id").toString()
            afterPrice = bundle.getLong("after_price")
            dropOffOpt = bundle.getString("drop_off_opt").toString()
            pickUpOpt = bundle.getString("pick_up_opt").toString()
            dropOff = bundle.getInt("drop_off")
            pickUp = bundle.getInt("pick_up")
            totalWeightPrice = bundle.getInt("total_price_weight")
            resultCalculation = bundle.getSerializable("hashItem") as HashMap<String, Int>
            discount = 0
            serviceName = bundle.getString("service_name").toString()
            servicePrice = bundle.getInt("service_price")
            servicePerKg = bundle.getInt("service_per_kg")
            serviceId = bundle.getString("service_id").toString()
            if (resultCalculation.isNotEmpty()) {
//                showMessage(resultCalculation["barang387"].toString())
//                showMessage(resultCalculation.keys.toString())
                getItem()
            } else {
                val message = "Gagal"
                showMessage(message)
            }

        }

        txtTitle.text = "Konfirmasi cucian #${orderId}"
        txtUserName.text = "Nama:\n" + username
        txtPhoneNumber.text = "No. HP:\n" + phoneNumber
        txtAddress.text = "Alamat:\n" + address
        txtOrder.text = "Terima\t:${orderDate.format(formatter)}"
        txtFinish.text = "Selesai\t:${finishDate.format(formatter)}"
        txtTotalWeight.text = totalWeight.toString() + "Kg"
        txtServiceName.text = serviceName
        txtServicePrice.text = "${currencyFormat.format(servicePrice)}/${servicePerKg}Kg"
        txtSubTotal.text = currencyFormat.format(totalWeightPrice).toString()
        txtDeliver.text = "Layanan Antar\n\nLayanan Jemput"
        txtDeliverPrice.text =
            "${currencyFormat.format(pickUp)}\n\n${currencyFormat.format(dropOff)}"
        txtDiscount.text = currencyFormat.format(discount).toString()
        txtPax.text = "${currencyFormat.format(0L)}"
        txtTotalPrice.text = "${currencyFormat.format(afterPrice)}"

        btnPay.setOnClickListener {
//            showInputDialog()
            apiService.insertOrder(
                orderId,
                phoneNumber,
                serviceId,
                "konfirmasi",
                totalWeightPrice,
                totalWeight,
                discount,
                afterPrice.toInt(),
                pickUpOpt,
                dropOffOpt
            ).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val message = response.body()!!.message
                        showMessage(message)
                        resultCalculation.keys.forEach {
                            apiService.insertOrderDetail(orderId, it, resultCalculation[it]!!)
                                .enqueue(object : Callback<ApiResponse> {
                                    override fun onResponse(
                                        call: Call<ApiResponse>,
                                        response: Response<ApiResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            val message = response.body()!!.message
                                            showMessage(message)
                                            showInputDialog()

                                        } else {
                                            val message = "Error 2.1: ${response.body()!!.message}"
                                            showMessage(message)
                                        }
                                    }

                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                        val message = "Error 2.2: $t"
                                        showMessage(message)
                                    }

                                })
                        }
                    } else {
                        val message = "Error 1: ${response.body()!!.message}"
                        showMessage(message)
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    val message = "Error 2: $t"
                    showMessage(message)
                }

            })
        }

    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pesanan telah dikirim")
        builder.setCancelable(false)

        val inputLayout = layoutInflater.inflate(R.layout.dialog_input, null)
        inputEditText = inputLayout.findViewById(R.id.inputEditText)
        inputEditText.text = "Pesanan anda telah masuk ke layanan kami.\n" +
                "Segera bayar melalui transfer atm dibawah ini:\n\n" +
                "(002) 3322001239454 A.N. Laundry Nasmi\n\n" +
                "Atau anda bisa bayar langsung ditempat anda atau landry kami.\n" +
                "Sekian, Terima Kasih."
        builder.setView(inputLayout)

        builder.setPositiveButton("OK") { dialog, _ ->
            Handler().postDelayed({
                val main = Intent(this@ConfirmActivity, MainActivity::class.java)
                val bundle = Bundle()
                bundle.putString("phone_number",phoneNumber)
                main.putExtras(bundle)
                startActivity(main)
                finish()
            }, 2000)
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun getItem() {
        apiService.getAllItem().enqueue(object : Callback<List<ItemModel>> {
            override fun onResponse(
                call: Call<List<ItemModel>>,
                response: Response<List<ItemModel>>
            ) {
                if (response.isSuccessful) {
                    listItem = response.body()!!
                    var name1 = StringBuilder()
                    var sum1 = StringBuilder()
                    listItem.forEach {
                        if (resultCalculation[it.item_id] != null) {
                            name1.append(it.name + " (~${it.weight}Kg)" + "\n")
                            sum1.append(resultCalculation[it.item_id].toString() + "\n")
                        }
                    }
                    txtItemName.text = name1
                    txtItemSum.text = sum1
                    progBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<ItemModel>>, t: Throwable) {
                progBar.visibility = View.GONE
                finish()
            }

        })

    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}