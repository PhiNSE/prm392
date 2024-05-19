package com.phinse.prm392

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val phoneOrEmail: String = findViewById<EditText>(R.id.etPhoneOrEmail).text.toString()
        val password: String = findViewById<EditText>(R.id.etPass).text.toString()
        val loginButton = findViewById<Button>(R.id.btnLogin2).setOnClickListener{
            if(login(phoneOrEmail, password)){
                Toast.makeText(this,"Login successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<CardView>(R.id.ivInsta).setOnClickListener{
            Intent(this, IstagramSignIn::class.java).apply {
                startActivity(this)
            }
        }
    }
    fun login(phoneOrEmail: String, password: String): Boolean {
        return true
    }
}