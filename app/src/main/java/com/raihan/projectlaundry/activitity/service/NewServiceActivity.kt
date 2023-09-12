package com.raihan.projectlaundry.activitity.service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.ItemModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.random.Random

class NewServiceActivity : AppCompatActivity() {

    val apiService = ApiClient.apiService
    lateinit var progBar: ProgressBar
    lateinit var imageView: ImageView
    lateinit var btnSelectImage: Button
    lateinit var editName: EditText
    lateinit var editPrice: EditText
    lateinit var editPerKg: EditText
    lateinit var editDesc: EditText
    lateinit var btnInsert: Button
    var selectedPhotoUri: Uri? = null
    var selectedPhotoPath: String? = null
    lateinit var checkboxContainer: LinearLayout
    private var itemStatusMap = mutableMapOf<ItemModel, Boolean>()
    private var service_id = "layanan" + generateRandomNumbers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_service)
        progBar = findViewById(R.id.progBar)
        imageView = findViewById(R.id.imageView)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnInsert = findViewById(R.id.btnInsert)
        editName = findViewById(R.id.editName)
        editPrice = findViewById(R.id.editPrice)
        editPerKg = findViewById(R.id.editPerKg)
        editDesc = findViewById(R.id.editDesc)
        checkboxContainer = findViewById(R.id.checkboxContainer)

        val actionBar = supportActionBar
        actionBar?.title = "Buat Layanan Baru"

        progBar.visibility = View.GONE

        getAllItem()

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
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION
                )
            }
        }

        btnInsert.setOnClickListener {
            progBar.visibility = View.VISIBLE
            registerService()
            for ((item, isChecked) in itemStatusMap) {
                if (isChecked) {
                    setLayout(service_id, item.item_id)
//                Toast.makeText(this,"Id : ${item_id}",Toast.LENGTH_LONG).show()
                }
            }
            progBar.visibility = View.GONE
//            val intent = Intent(this, ServiceActivity::class.java)
//            startActivity(intent)
//            finish()
        }
    }

    fun getAllItem() {
        apiService.getAllItem().enqueue(object : Callback<List<ItemModel>> {
            override fun onResponse(
                call: Call<List<ItemModel>>,
                response: Response<List<ItemModel>>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()!!

                    for (item in items) {
                        val checkBox = CheckBox(this@NewServiceActivity)
                        checkBox.text = item.name
                        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                            itemStatusMap[item] = isChecked
                        }

                        checkboxContainer.addView(checkBox)
                    }
                }
            }

            override fun onFailure(call: Call<List<ItemModel>>, t: Throwable) {
                val message = "Error : $t"
                showMessage(message)
            }

        })
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

    private fun registerService() {
        val intent = Intent(this, ServiceActivity::class.java)
        val name = editName.text.toString()
        val price = editPrice.text.toString()
        val perKg = editPerKg.text.toString()
        val desc = editDesc.text.toString()
        var itemId = mutableListOf<String>()
        for ((item, isChecked) in itemStatusMap) {
            if (isChecked) {
                itemId.add(item.item_id)
//                Toast.makeText(this,"Id : ${item_id}",Toast.LENGTH_LONG).show()
            }
        }
        if (name.isNotEmpty() && price.isNotEmpty() && desc.isNotEmpty() && perKg.isNotEmpty() && selectedPhotoPath != null && itemId.isNotEmpty()) {
            val file = File(selectedPhotoPath)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-file"), file)
            val photo: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val serviceIdBody = RequestBody.create(MediaType.parse("text/plain"), service_id)
            val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
            val priceBody = RequestBody.create(MediaType.parse("text/plain"), price)
            val perKgBody = RequestBody.create(MediaType.parse("text/plain"), perKg)
            val descBody = RequestBody.create(MediaType.parse("text/plain"), desc)
            apiService.insertService(
                serviceIdBody,
                nameBody,
                descBody,
                priceBody,
                perKgBody,
                photo
            ).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
//                        progBar.visibility = View.GONE
                        val apiResponse = response.body()
                        // Proses respons dari server
                        itemId.forEach{
                            setLayout(service_id,it)
                        }
                        val message = apiResponse!!.message
                        showMessage(message)
                        startActivity(intent)
                        finish()

                    } else {
                        // Tangani kesalahan saat komunikasi dengan server
                        progBar.visibility = View.GONE
                        var message = "Error 1: " + response.body() + " " + response.body()
                        showMessage(message)
                        finish()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Tangani kesalahan jaringan atau kesalahan lainnya
                    progBar.visibility = View.GONE
                    var message = "Error 2: + $t"
                    showMessage(message)
                }
            })


        } else {
            progBar.visibility = View.GONE
            Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
        }
    }

    fun setLayout(service_id: String, item_id: String) {
        apiService.layoutService(
            service_id,
            item_id
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    val message = response.body()!!.message
                    showMessage(message)
                } else {
                    val message = "Error 3: ${response.body()!!.message}"
                    showMessage(message)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                val message = "Error 4: $t"
                showMessage(message)
            }

        })
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 9544
        private const val REQUEST_STORAGE_PERMISSION = 2
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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