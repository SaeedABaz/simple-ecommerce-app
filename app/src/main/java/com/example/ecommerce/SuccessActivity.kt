package com.example.ecommerce


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {

    private lateinit var returnHomeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        returnHomeButton = findViewById(R.id.return_home_button)

        returnHomeButton.setOnClickListener {
            // Navigate back to the main page
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
