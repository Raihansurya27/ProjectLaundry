package com.raihan.projectlaundry.activitity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RegisterActivity : AppCompatActivity() {

    lateinit var phoneNumber: EditText
    lateinit var username: EditText
    lateinit var password: TextInputEditText
    lateinit var passwordLayout: TextInputLayout
    lateinit var address: EditText
    lateinit var register: Button
    var selectedPhotoUri: Uri? = null
    var selectedPhotoPath: String? = null
    lateinit var btnSelectImage: Button
    lateinit var imageView: ImageView
//    lateinit var progBar:ProgressBar

    val apiService = ApiClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        password = findViewById(R.id.password)
        passwordLayout = findViewById(R.id.password_layout)
        phoneNumber = findViewById(R.id.phoneNumber)
        username = findViewById(R.id.username)
        address = findViewById(R.id.address)
        register = findViewById(R.id.register)
//        progBar = findViewById(R.id.progBar)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        imageView = findViewById(R.id.image)

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


        btnSelectImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openImagePicker()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )
            }
        }


        register.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val dialogMessage = layoutInflater.inflate(R.layout.dialog_message, null)
        val text = dialogMessage.findViewById<TextView>(R.id.messageText)
        val image = dialogMessage.findViewById<ImageView>(R.id.messageImage)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(dialogMessage)
        val login = Intent(this, LoginActivity::class.java)
        val name = username.text.toString()
        val phone = phoneNumber.text.toString()
        val password = password.text.toString()
        val address = address.text.toString()
        val role = "Pelanggan"
        if (name.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && address.isNotEmpty() && selectedPhotoPath != null) {
            val file = File(selectedPhotoPath)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-file"), file)
            val photo: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val usernameBody = RequestBody.create(MediaType.parse("text/plain"), name)
            val phoneNumberBody = RequestBody.create(MediaType.parse("text/plain"), phone)
            val passwordBody = RequestBody.create(MediaType.parse("text/plain"), password)
            val addressBody = RequestBody.create(MediaType.parse("text/plain"), address)
            val roleBody = RequestBody.create(MediaType.parse("text/plain"), role)
            apiService.register(usernameBody,
                phoneNumberBody,
                passwordBody,
                addressBody,
                roleBody,
                photo).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if(response.isSuccessful){
                        builder.setTitle("Registrasi Suskes")
                        image.setImageResource(android.R.drawable.checkbox_on_background)
                        text.setText("Registrasi sukses, silahkan munuju halaman login")

                        builder.setPositiveButton("Menuju halaman login") { dialog, _ ->
                            startActivity(login)
                            dialog.dismiss()
                            finish()
                        }

                        val dialog = builder.create()
                        dialog.show()
                    }else{
                        builder.setTitle("Registrasi Gagal")
                        image.setImageResource(android.R.drawable.ic_delete)
                        text.setText("Inputan ada yang invalid atau salah.\nSilahkan cek kembali")

                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }


                        val dialog = builder.create()
                        dialog.show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    builder.setTitle("Inputan invalid")
                    image.setImageResource(android.R.drawable.ic_delete)
                    text.setText("Inputan ada yang invalid, salah, atau terkendala jaringan.\nSilahkan cek kembali\n$t")

                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }


                    val dialog = builder.create()
                    dialog.show()
                }

            })
        } else {
            builder.setTitle("Inputan Kosong")
            image.setImageResource(android.R.drawable.ic_delete)
            text.setText("Ada inputan kosong, silahkan isi terlebih dahulu")

            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }


            val dialog = builder.create()
            dialog.show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImagePicker() {
        val pick = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pick, IMAGE_PICK_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK) {
            // Ambil URI foto yang dipilih
            selectedPhotoUri = data?.data
            selectedPhotoPath = getRealPathFromUri(selectedPhotoUri)
            // Set foto ke ImageView
            imageView.setImageURI(selectedPhotoUri)
        }
    }

    private fun getRealPathFromUri(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null) {
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(projection[0])
            val path_image = cursor.getString(columnIndex!!)
            if (path_image != null) {
                cursor?.close()
                return path_image
            }
        }
//        val path = cursor?.getString(columnIndex!!)
        cursor?.close()
        return ""
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 9544
        private const val REQUEST_STORAGE_PERMISSION = 2
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}