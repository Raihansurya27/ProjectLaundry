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
import com.bumptech.glide.Glide
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

class UpdateItemActivity : AppCompatActivity() {

    val apiService = ApiClient.apiService
    lateinit var progBar: ProgressBar
    lateinit var imageView: ImageView
    lateinit var btnSelectImage: Button
    lateinit var editName: EditText
    lateinit var editWeight: EditText
    lateinit var editDesc: EditText
    lateinit var btnUpdate: Button
    var selectedPhotoUri: Uri? = null
    var selectedPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_item)
        progBar = findViewById(R.id.progBar)
        imageView = findViewById(R.id.imageView)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        editDesc = findViewById(R.id.editDesc)
        editName = findViewById(R.id.editName)
        editWeight = findViewById(R.id.editWeight)
        btnUpdate = findViewById(R.id.btnUpdate)
        var itemId: String = ""

        val actionBar = supportActionBar
        actionBar?.title = "Ubah Barang Cucian"

        val bundle: Bundle? = intent.extras

        if (bundle != null) {
            itemId = bundle.getString("item_id").toString()
        }

        apiService.getItem(itemId).enqueue(object : Callback<List<ItemModel>> {
            override fun onResponse(
                call: Call<List<ItemModel>>,
                response: Response<List<ItemModel>>
            ) {
                if (response.isSuccessful) {
                    val item = response.body()!!

                    progBar.visibility = View.GONE
                    putDataIntoForm(item)
                } else {
                    var message = "Gagal 5 \n\n${response.body()}"
                    showMessage(message)
                }
            }

            override fun onFailure(call: Call<List<ItemModel>>, t: Throwable) {
                finish()
            }

        })

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
            val intent = Intent(this, ItemActivity::class.java)
            progBar.visibility = View.VISIBLE
            getItem(itemId)
            startActivity(intent)
            finish()
        }

    }

    fun putDataIntoForm(item: List<ItemModel>) {
        if (item.get(0).picture.trim() != null) {
            Glide
                .with(this)
                .load("https://laundrynajmi.000webhostapp.com/${item.get(0).picture}")
                .into(imageView)
        }

        editName.setText(item.get(0).name)
        editWeight.setText(item.get(0).weight.toString())
        editDesc.setText(item.get(0).desc)

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

    fun getItem(itemId: String) {
        val name = editName.text.toString()
        val weight = editWeight.text.toString().toFloat()
        val desc = editDesc.text.toString()
        if (name.isNotEmpty() && weight.toString().isNotEmpty() && desc.isNotEmpty()) {
            if (selectedPhotoPath != null) {
                val file = File(selectedPhotoPath)
                val requestFile: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-file"), file)
                val photo: MultipartBody.Part =
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                val itemIdBody = RequestBody.create(MediaType.parse("text/plain"), itemId)
                val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
                val weightBody =
                    RequestBody.create(MediaType.parse("text/plain"), weight.toString())
                val descBody = RequestBody.create(MediaType.parse("text/plain"), desc)
                apiService.updateItem1(
                    itemIdBody,
                    nameBody,
                    weightBody,
                    descBody,
                    photo
                ).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {
                        if (response.isSuccessful) {
//                            progBar.visibility = View.GONE
                            val apiResponse = response.body()
                            var message = "${apiResponse!!.message}"
                            showMessage(message)

                        } else {
                            var message = "${response.body()!!.message}"
                            showMessage(message)
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        var message = "Gagal 2 $call\n \n$t"
                        showMessage(message)
                    }

                })
            } else {
                val itemIdBody = RequestBody.create(MediaType.parse("text/plain"), itemId)
                val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
                val weightBody =
                    RequestBody.create(MediaType.parse("text/plain"), weight.toString())
                val descBody = RequestBody.create(MediaType.parse("text/plain"), desc)
                apiService.updateItem2(
                    itemIdBody,
                    nameBody,
                    weightBody,
                    descBody,
                ).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {
                        if (response.isSuccessful) {
                            progBar.visibility = View.GONE
                            val apiResponse = response.body()
                            var message = "${apiResponse!!.message}"
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