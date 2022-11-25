package com.ssafy.smartstore.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.ssafy.smartstore.LoginActivity
import com.ssafy.smartstore.R

class SplashActivity : AppCompatActivity() {

    private lateinit var ivGIF: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ivGIF = findViewById<ImageView>(R.id.imageView_gif)
        Glide.with(this).load(R.drawable.loading_infinity)
            .fitCenter()
            .override(Target.SIZE_ORIGINAL)
            .into(ivGIF)

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        },2000)

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}