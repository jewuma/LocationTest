package com.example.locationtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_page)
    }
    @Suppress("UNUSED_PARAMETER")
    fun startMainScreen(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: Beende den Startbildschirm, sobald die MainActivity gestartet ist
    }
}