package com.raihan.projectlaundry.activitity.item

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import kotlin.random.Random

class NewItemActivity : AppCompatActivity() {

    val apiService = ApiClient.apiService
    lateinit var progBar: ProgressBar
    lateinit var imageView: ImageView
    lateinit var btnSelectImage: Button
    lateinit var editName:EditText
    lateinit var editWeight:EditText
    lateinit var editDesc:EditText
    lateinit var btnInsert:Button
    var selectedPhotoUri: Uri? = null
    var selectedPhotoPath: String? = null
    var itemId = "barang"+generateRandomNumbers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)
        progBar = findViewById(R.id.progBar)
        imageView = findViewById(R.id.imageView)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        editName = findViewById(R.id.editName)
        editDesc = findViewById(R.id.editDesc)
        editWeight = findViewById(R.id.editWeight)
        btnInsert = findViewById(R.id.btnInsert)

        val actionBar = supportActionBar
        actionBar?.title = "Buat Barang Cucian Baru"

        progBar.visibility = View.GONE

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

        btnInsert.setOnClickListener {
            progBar.visibility = View.VISIBLE
            registerItem()
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

    private fun registerItem() {
        val intent = Intent(this, ItemActivity::class.java)
        val name = editName.text.toString()
        val weight = editWeight.text.toString().toFloat()
        val desc = editDesc.text.toString()
        if (name.isNotEmpty() && weight.toString().isNotEmpty() && desc.isNotEmpty() && selectedPhotoPath != null) {
            val file = File(selectedPhotoPath)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-file"), file)
            val photo: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val itemIdBody = RequestBody.create(MediaType.parse("text/plain"), itemId)
            val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
            val weightBody = RequestBody.create(MediaType.parse("text/plain"), weight.toString())
            val descBody = RequestBody.create(MediaType.parse("text/plain"), desc)
            apiService.insertItem(
                itemIdBody,
                nameBody,
                weightBody,
                descBody,
                photo
            ).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        progBar.visibility = View.GONE
                        val apiResponse = response.body()
                        // Proses respons dari server
                        var message = "${apiResponse!!.message}"
                        showMessage(message)
                        startActivity(intent)
                        finish()
                    } else {
                        // Tangani kesalahan saat komunikasi dengan server
                        progBar.visibility = View.GONE
                        var message = "Gagal 1 " + response.body()!!.message
                        showMessage(message)
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Tangani kesalahan jaringan atau kesalahan lainnya
                    progBar.visibility = View.GONE
                    var message = "Gagal 2 + $t"
                    showMessage(message)
                }
            })

        } else {
            progBar.visibility = View.GONE
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

    fun generateRandomNumbers(): String{
        val random = Random(System.currentTimeMillis())
        var result = ""

        repeat(3) {
            val randomNumber = random.nextInt(10)
            result = result + randomNumber
        }

        return result
    }
}