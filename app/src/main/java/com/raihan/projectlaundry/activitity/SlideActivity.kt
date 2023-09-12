package com.raihan.projectlaundry.activitity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.adapter.SliderAdapter
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderView

class SlideActivity : AppCompatActivity() {

    // on below line we are creating a variable
    // for our array list for storing our images.

    // on below line we are creating
    // a variable for our slider view.
    lateinit var sliderView: SliderView

    // on below line we are creating
    // a variable for our slider adapter.
    lateinit var sliderAdapter: SliderAdapter

    lateinit var buttonSkip: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide)

        // on below line we are initializing our slier view.
        sliderView = findViewById(R.id.slider)
        buttonSkip = findViewById(R.id.buttonSkip)

        val actionBar = supportActionBar
        actionBar?.title = "Selamat Datang"

        // on below line we are initializing
        // our image url array list.

        // on below line we are adding data to our image url array list.
        val imageUrl = intArrayOf(R.drawable.asset1, R.drawable.asset2, R.drawable.asset3, R.drawable.asset4)

        val textTitle = arrayOf("Rapi","Bersih","Harum","Murah")

        val textDesc = arrayOf("Pakaian dilipat dengan rapi","Menggunakan deterjen ramah lingkungan dan bersih melawan kotoran","Menggunakan pewangi alami","Harga terjangkau")

        // on below line we are initializing our
        // slider adapter and adding our list to it.
        sliderAdapter = SliderAdapter(imageUrl,textTitle,textDesc)

        // on below line we are setting auto cycle direction
        // for our slider view from left to right.
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        // on below line we are setting adapter for our slider.
        sliderView.setSliderAdapter(sliderAdapter)

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

        // on below line we are setting scroll time
        // in seconds for our slider view.
        sliderView.scrollTimeInSec = 3

        // on below line we are setting auto cycle
        // to true to auto slide our items.
        sliderView.isAutoCycle = true

        // on below line we are calling start
        // auto cycle to start our cycle.
        sliderView.startAutoCycle()

        buttonSkip.setOnClickListener {
            val skip = Intent(this, LoginActivity::class.java)
            startActivity(skip)
            finish()
        }

    }
}