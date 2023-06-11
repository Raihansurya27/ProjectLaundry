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
import com.bumptech.glide.Glide
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ApiResponse
import com.raihan.projectlaundry.model.ItemModel
import com.raihan.projectlaundry.model.LayoutModel
import com.raihan.projectlaundry.model.ServiceModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateServiceActivity : AppCompatActivity() {

    val apiService = ApiClient.apiService
    lateinit var progBar: ProgressBar
    lateinit var imageView: ImageView
    lateinit var btnSelectImage: Button
    lateinit var editName: EditText
    lateinit var editPrice: EditText
    lateinit var editPerKg: EditText
    lateinit var editDesc: EditText
    lateinit var btnUpdate: Button
    var selectedPhotoUri: Uri? = null
    var selectedPhotoPath: String? = null
    lateinit var checkboxContainer: LinearLayout
    private var itemStatusMap = mutableMapOf<ItemModel, Boolean>()
    var layouts = listOf<LayoutModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_service)
        progBar = findViewById(R.id.progBar)
        imageView = findViewById(R.id.imageView)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnUpdate = findViewById(R.id.btnUpdate)
        editDesc = findViewById(R.id.editDesc)
        editName = findViewById(R.id.editName)
        editPrice = findViewById(R.id.editPrice)
        editPerKg = findViewById(R.id.editPerKg)
        checkboxContainer = findViewById(R.id.checkboxContainer)

        val actionBar = supportActionBar
        actionBar?.title = "Ubah Layanan Cucian"

        val bundle: Bundle? = intent.extras
        var serviceId = ""

        if (bundle != null) {
            serviceId = bundle.getString("service_id").toString()
//            Toast.makeText(this,serviceId,Toast.LENGTH_LONG).show()
        }

        apiService.getService(serviceId).enqueue(object: Callback<List<ServiceModel>>{
            override fun onResponse(
                call: Call<List<ServiceModel>>,
                response: Response<List<ServiceModel>>
            ) {
                if(response.isSuccessful){
                    val service = response.body()!!
                    progBar.visibility = View.GONE
                    putDataIntoForm(service)
                    getAllLayout()
                    getAllItem(service[0].service_id)

                }else{
                    val message = "Gagal 4 \n\n${response.body()}"
                    showMessage(message)
                }
            }

            override fun onFailure(call: Call<List<ServiceModel>>, t: Throwable) {
                val message = "Gagal 5 \n\n$t"
                showMessage(message)
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
            progBar.visibility = View.VISIBLE
            getService(serviceId)
            for ((item, isChecked) in itemStatusMap) {
                if (isChecked) {
                    setLayout(serviceId, item.item_id)
                }
            }
            progBar.visibility = View.GONE
        }

    }

    fun setLayout(service_id: String, item_id: String) {
        apiService.deleteLayout(service_id).enqueue(object :Callback<ApiResponse>{
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful){
                    val message = "berhasil"
                    showMessage(message)
                }else{
                    val message = "Error 1: ${response.body()!!.message}"
                    showMessage(message)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                val message = "Error: $t"
                showMessage(message)
            }

        })

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

    fun putDataIntoForm(service: List<ServiceModel>) {
        if (service.get(0).picture.trim() != null) {
            Glide
                .with(this)
                .load("https://laundrynajmi.000webhostapp.com/${service[0].picture}")
                .into(imageView)
        }

        editName.setText(service[0].name)
        editPrice.setText(service[0].price.toString())
        editPerKg.setText(service[0].per_kg.toString())
        editDesc.setText(service[0].desc)

    }

    fun getAllLayout(){
        apiService.getAllLayout().enqueue(object :Callback<List<LayoutModel>>{
            override fun onResponse(
                call: Call<List<LayoutModel>>,
                response: Response<List<LayoutModel>>
            ) {
                if (response.isSuccessful){
                    layouts = response.body()!!
                }else{
                    val message = "Error : ${response.body()}"
                    showMessage(message)
                }
            }

            override fun onFailure(
                call: Call<List<LayoutModel>>,
                t: Throwable
            ) {
                val message = "Error : $t"
                showMessage(message)
            }

        })
    }

    fun getAllItem(service: String){
        apiService.getAllItem().enqueue(object :Callback<List<ItemModel>>{
            override fun onResponse(
                call: Call<List<ItemModel>>,
                response: Response<List<ItemModel>>
            ) {
                if (response.isSuccessful){
                    val items = response.body()!!

                    for (item in items){
                        val checkBox = CheckBox(this@UpdateServiceActivity)
                        checkBox.text = item.name
                        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                            itemStatusMap[item] = isChecked

                        }

                        for (layout in layouts){
                            if (item.item_id == layout.item_id && service == layout.service_id){
                                checkBox.isChecked = true
                            }
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

    fun getService(serviceId: String) {
        val intent = Intent(this, ServiceActivity::class.java)
        val name = editName.text.toString()
        val price = editPrice.text.toString()
        val perKg = editPerKg.text.toString()
        val desc = editDesc.text.toString()
        var item_id = mutableListOf<String>()
        for((item,isChecked) in itemStatusMap){
            if(isChecked){
//                Toast.makeText(this,"Id : ${item.item_id}",Toast.LENGTH_LONG).show()
                item_id.add(item.item_id)
            }
        }
        if (name.isNotEmpty() && price.isNotEmpty() && perKg.isNotEmpty() && desc.isNotEmpty() && item_id.isNotEmpty()) {
            if (selectedPhotoPath != null) {
                val file = File(selectedPhotoPath)
                val requestFile: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-file"), file)
                val photo: MultipartBody.Part =
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                val serviceIdBody = RequestBody.create(MediaType.parse("text/plain"), serviceId)
                val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
                val priceBody =
                    RequestBody.create(MediaType.parse("text/plain"), price)
                val perKgBody =
                    RequestBody.create(MediaType.parse("text/plain"), perKg)
                val descBody = RequestBody.create(MediaType.parse("text/plain"), desc)
                apiService.updateService1(
                    serviceIdBody,
                    nameBody,
                    descBody,
                    priceBody,
                    perKgBody,
                    photo
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
                    }

                })
            } else {
                val serviceIdBody = RequestBody.create(MediaType.parse("text/plain"), serviceId)
                val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
                val priceBody =
                    RequestBody.create(MediaType.parse("text/plain"), price)
                val perKgBody =
                    RequestBody.create(MediaType.parse("text/plain"), perKg)
                val descBody = RequestBody.create(MediaType.parse("text/plain"), desc)
                apiService.updateService2(
                    serviceIdBody,
                    nameBody,
                    descBody,
                    priceBody,
                    perKgBody,
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