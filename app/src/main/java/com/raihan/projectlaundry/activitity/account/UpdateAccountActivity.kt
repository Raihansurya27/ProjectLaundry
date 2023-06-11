package com.raihan.projectlaundry.activitity.account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.UserModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateAccountActivity : AppCompatActivity() {

    private lateinit var progBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnUpdate: Button
    private lateinit var editUsername: EditText
    private lateinit var editPhoneNumber: EditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var editPassword: TextInputEditText
    private lateinit var editAddress: EditText
    private lateinit var roleGroup: RadioGroup
    lateinit var phoneNumber: String
    val apiService = ApiClient.apiService
    var selectedPhotoUri: Uri? = null
    var selectedPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_account)

        progBar = findViewById(R.id.progBar)
        imageView = findViewById(R.id.imageView)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        editUsername = findViewById(R.id.editUserName)
        editPhoneNumber = findViewById(R.id.editPhoneNumber)
        passwordLayout = findViewById(R.id.password_layout)
        editPassword = findViewById(R.id.editPassword)
        editAddress = findViewById(R.id.editAddress)
        roleGroup = findViewById(R.id.rg1)
        btnUpdate = findViewById(R.id.btnUpdate)
        editPhoneNumber.isEnabled = false
        val actionBar = supportActionBar
        actionBar?.title = "Ubah Akun"

        progBar.visibility = View.VISIBLE

        val bundle: Bundle? = intent.extras

        if (bundle != null) {
            phoneNumber = bundle.getString("phone_number").toString()
        }



        apiService.getUser(phoneNumber).enqueue(object : Callback<List<UserModel>> {
            override fun onResponse(
                call: Call<List<UserModel>>,
                response: Response<List<UserModel>>
            ) {
                if (response.isSuccessful) {
                    val user = response.body()!!

                    progBar.visibility = View.GONE
                    putDataIntoForm(user)
                } else {
                    finish()
                }
            }

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                var message = "Gagal 5 \n\n$t"
                showMessage(message)
            }

        })


        passwordLayout.setEndIconOnClickListener {
            val passwordVisible =
                editPassword.transformationMethod is PasswordTransformationMethod
            if (passwordVisible) {
                editPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            editPassword.setSelection(editPassword.text!!.length)
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

        btnUpdate.setOnClickListener {
            progBar.visibility = View.VISIBLE
            getUser(phoneNumber)
        }

    }

    fun putDataIntoForm(user: List<UserModel>) {
        if (user.get(0).picture.trim() != null) {
            Glide
                .with(this)
                .load("https://laundrynajmi.000webhostapp.com/${user.get(0).picture}")
                .into(imageView)
        }

        editUsername.setText(user.get(0).username)
        editPassword.setText(user.get(0).password)
        editAddress.setText(user.get(0).address)
        editPhoneNumber.setText(user.get(0).phone_number)

        val radioButtonCount = roleGroup.childCount

        for (i in 0 until radioButtonCount) {
            val roleButton = roleGroup.getChildAt(i) as RadioButton

            if (roleButton.text.toString() == user.get(0).role) {
                roleButton.isChecked = true
                break
            }
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
            Glide
                .with(this)
                .load(selectedPhotoUri)
                .into(imageView)
//            imageView.setImageURI(selectedPhotoUri)
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

    fun getUser(phoneNumber: String) {
        val intent = Intent(this, AccountActivity::class.java)
        val name = editUsername.text.toString()
        val password = editPassword.text.toString()
        val address = editAddress.text.toString()
        val phone = phoneNumber
        val role = findViewById<RadioButton>(roleGroup.checkedRadioButtonId).text.toString()
        if (name.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && address.isNotEmpty() && role.isNotEmpty()) {
            if(selectedPhotoPath != null){
                val file = File(selectedPhotoPath)
                val requestFile: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-file"), file)
                val photo: MultipartBody.Part =
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                val usernameBody = RequestBody.create(MediaType.parse("text/plain"), name)
                val addressBody = RequestBody.create(MediaType.parse("text/plain"), address)
                val roleBody = RequestBody.create(MediaType.parse("text/plain"), role)
                val passwordBody = RequestBody.create(MediaType.parse("text/plain"), password)
                val phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone)
                apiService.updateUser1(
                    phoneBody,
                    usernameBody,
                    passwordBody,
                    addressBody,
                    roleBody,
                    photo
                ).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            progBar.visibility = View.GONE
                            val apiResponse = response.body()
                            var message = "${apiResponse!!.message}"
                            showMessage(message)
                            startActivity(intent)
                            finish()
                        } else {
                            var message = "${response.body()!!.message}"
                            showMessage(message)
                        }
                    }
                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        var message = "Gagal 2 $call\n \n$t"
                        showMessage(message)
                        message = "$phoneBody\n$usernameBody\n$addressBody\n$roleBody\n$passwordBody\n$photo"
                        showMessage(message)
                    }

                })
            }else{
                val usernameBody = RequestBody.create(MediaType.parse("text/plain"), name)
                val addressBody = RequestBody.create(MediaType.parse("text/plain"), address)
                val roleBody = RequestBody.create(MediaType.parse("text/plain"), role)
                val passwordBody = RequestBody.create(MediaType.parse("text/plain"), password)
                val phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone)
                apiService.updateUser2(
                    phoneBody,
                    usernameBody,
                    passwordBody,
                    addressBody,
                    roleBody,
                ).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            progBar.visibility = View.GONE
                            val apiResponse = response.body()
                            var message = "${apiResponse!!.message}"
                            startActivity(intent)
                            showMessage(message)
                            finish()
                        } else {
                            var message = "${response.body()!!.message}"
                            showMessage(message)
                        }
                    }
                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        var message = "Gagal 3 $call\n \n$t"
                        showMessage(message)
                        message = "$phoneBody\n$usernameBody\n$addressBody\n$roleBody\n$passwordBody"
                        showMessage(message)
                    }

                })
            }
        } else {
            Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 9544
        private const val REQUEST_STORAGE_PERMISSION = 2
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}