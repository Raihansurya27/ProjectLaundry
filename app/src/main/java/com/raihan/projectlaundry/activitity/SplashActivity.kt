package com.raihan.projectlaundry.activitity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.raihan.projectlaundry.R

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val backgroundImage : ImageView = findViewById(R.id.imageSplash)
        val animationSlide = AnimationUtils.loadAnimation(this,R.anim.slide_anim)
        backgroundImage.startAnimation(animationSlide)
        Handler().postDelayed({
            val intent = Intent(this,SlideActivity::class.java)
            startActivity(intent)
            finish()
        },5000)
    }
}