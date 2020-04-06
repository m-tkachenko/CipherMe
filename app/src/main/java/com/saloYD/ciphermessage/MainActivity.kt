package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_up_button.setOnClickListener {

            val username = edit_username.text.toString()
            Log.d("MainActivity", "Username: $username")

            val email = edit_email.text.toString()
            Log.d("MainActivity", "Email: $email")

            val password = edit_password.text.toString()
            Log.d("MainActivity", "Password: $password")
        }

        button_textview_account.setOnClickListener{
            Log.d("MainActivity", "Try to show LoginActivity")

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

    }
}
