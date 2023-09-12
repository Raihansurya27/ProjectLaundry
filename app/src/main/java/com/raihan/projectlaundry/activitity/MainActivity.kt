package com.raihan.projectlaundry.activitity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.databinding.ActivityMainBinding
import com.raihan.projectlaundry.model.UserModel
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val apiService = ApiClient.apiService
    var phoneNumberResult = ""
    var usernameResult = ""
    var addressResult = ""
    var pictureResult = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Pesan Muncul", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val username: TextView = headerView.findViewById(R.id.txtUsername)
        val phoneNumber: TextView = headerView.findViewById(R.id.txtPhoneNumber)
        val address: TextView = headerView.findViewById(R.id.txtAddress)
        val imageProfile :ImageView = headerView.findViewById(R.id.image)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val bundle :Bundle? = intent.extras

        if(bundle != null){
            phoneNumberResult = bundle.getString("phone_number").toString()
//            usernameResult = bundle.getString("username").toString()
//            addressResult = bundle.getString("address").toString()
//            pictureResult = bundle.getString("picture").toString()

            apiService.getUser(phoneNumber.text.toString()).enqueue(object :
                retrofit2.Callback<List<UserModel>> {
                override fun onResponse(
                    call: Call<List<UserModel>>,
                    response: Response<List<UserModel>>
                ) {
                    if (response.isSuccessful) {
                        val data: List<UserModel>? = response.body()
                        val bundle = Bundle()
                        data!!.forEach { item ->
                            usernameResult = item.username
                            addressResult = item.address
                            pictureResult = "https://laundrynajmi.000webhostapp.com/${item.picture}"
                        }

                    } else {

                    }
                }

                override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                    // Tangani kegagalan pemanggilan API
                }
            })

            phoneNumber.text = phoneNumberResult
            username.text = usernameResult
            address.text = addressResult
            Glide
                .with(this)
                .load(pictureResult)
                .into(imageProfile)


            navController.navigate(R.id.nav_home, bundle)
        }

    }

    fun logout(){
        // Hapus data sesi atau lakukan operasi logout lainnya di sini

        // Contoh: Menghapus data sesi
        val sharedPref = getSharedPreferences("session", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        // Buka halaman login
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings ->{

            true
        }
        R.id.action_logout->{
            logout()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}