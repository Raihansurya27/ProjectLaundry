package com.raihan.projectlaundry.activitity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.UserModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    lateinit var buttonRegister: Button
    lateinit var buttonLogin: Button
    lateinit var phoneNumber: EditText
    lateinit var password: TextInputEditText
    lateinit var passwordLayout :TextInputLayout

    val apiService = ApiClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonLogin = findViewById(R.id.buttonLogin)
        phoneNumber = findViewById(R.id.phone_number)
        password = findViewById(R.id.password)
        passwordLayout = findViewById(R.id.password_layout)

        val dialogMessage = layoutInflater.inflate(R.layout.dialog_message, null)
        val text = dialogMessage.findViewById<TextView>(R.id.messageText)
        val image = dialogMessage.findViewById<ImageView>(R.id.messageImage)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogMessage)
        val dialog = builder.create()
        var userRole = ""

        val actionBar = supportActionBar
        actionBar?.title = "Login"

//        val progBar = findViewById<ProgressBar>(R.id.progBar)

        passwordLayout.setEndIconOnClickListener {
            val passwordVisible =
                password.transformationMethod is PasswordTransformationMethod
            if (passwordVisible) {
                password.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            password.setSelection(password.text!!.length)
        }


        //Ke Register
        buttonRegister.setOnClickListener {
            val register = Intent(this, RegisterActivity::class.java)
//            progBar.visibility = View.VISIBLE
            startActivity(register)
        }

        buttonLogin.setOnClickListener {
            val user = Intent(this, MainActivity::class.java)
            val admin = Intent(this, AdminActivity::class.java)
            lifecycleScope.launch {
                try {
                    val response =
                        apiService.login(phoneNumber.text.toString(), password.text.toString())
                    if (response.isSuccessful) {
//                        val loginResponse = response.body()
                        builder.setTitle("Login Suskes")
                        text.text = "Login sukses, tunggu sebentar"
                        dialog.show()

                        apiService.getUser(phoneNumber.text.toString()).enqueue(object :
                            Callback<List<UserModel>> {
                            override fun onResponse(
                                call: Call<List<UserModel>>,
                                response: Response<List<UserModel>>
                            ) {
                                if (response.isSuccessful) {
                                    val data: List<UserModel>? = response.body()
                                    val bundle = Bundle()
                                    data!!.forEach { item ->
//                                        bundle.putString("username", item.username)
                                        bundle.putString("phone_number", item.phone_number)
//                                        bundle.putString("address", item.address)
//                                        bundle.putString("picture","https://laundrynajmi.000webhostapp.com/${item.picture}")
                                        userRole = item.role
                                    }

                                    if (userRole == "Admin" || userRole == "Super Admin") {
                                        Handler().postDelayed({
//                                            progBar.visibility = View.GONE
                                            dialog.dismiss()
                                            admin.putExtras(bundle)
                                            startActivity(admin)
                                            finish()
                                        }, 5000)
                                    } else {
                                        Handler().postDelayed({
                                            dialog.dismiss()
                                            user.putExtras(bundle)
                                            startActivity(user)
                                            finish()
                                        }, 5000)
                                    }


                                } else {

                                }
                            }

                            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                                // Tangani kegagalan pemanggilan API
                            }
                        })
                    } else {
                        // Tangani kesalahan saat permintaan login tidak berhasil
//                        val errorBody = response.errorBody()?.string()
                        // Lakukan sesuatu dengan pesan kesalahan

                        builder.setTitle("Login Gagal")
//                        image.setImageResource(android.R.drawable.ic_delete)
                        text.setText("Inputan ada yang invalid atau salah.\nSilahkan cek kembali")

                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                } catch (e: Exception) {
                    // Tangani kesalahan saat terjadi kesalahan jaringan atau lainnya
                }
            }
        }
    }
}