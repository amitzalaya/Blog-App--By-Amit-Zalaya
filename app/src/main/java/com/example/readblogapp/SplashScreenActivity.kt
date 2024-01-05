package com.example.readblogapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white))


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        waitForWhile()


    }

    //  go to main Activity
    private fun waitForWhile() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,RagisterActivity::class.java))
            finish()
        }, 5000)
    }


}