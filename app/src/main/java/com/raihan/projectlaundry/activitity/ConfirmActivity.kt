package com.raihan.projectlaundry.activitity


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R

class ConfirmActivity : AppCompatActivity(){

    private lateinit var inputEditText: TextView
    private lateinit var btnPay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        btnPay = findViewById(R.id.btnPay)

        btnPay.setOnClickListener {
            showInputDialog()
        }

    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pesanan telah dikirim")
        builder.setCancelable(true)

        val inputLayout = layoutInflater.inflate(R.layout.dialog_input, null)
        inputEditText = inputLayout.findViewById(R.id.inputEditText)
        inputEditText.text = "Pesanan anda telah masuk ke layanan kami.\n" +
                "Segera bayar melalui transfer atm dibawah ini:\n\n" +
                "(002) 3322001239454 A.N. Laundry Nasmi\n\n" +
                "Atau anda bisa bayar langsung ditempat anda atau landry kami.\n" +
                "Sekian, Terima Kasih."
        builder.setView(inputLayout)

        builder.setPositiveButton("OK") { dialog, _ ->

            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}