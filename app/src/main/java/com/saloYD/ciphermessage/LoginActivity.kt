package com.saloYD.ciphermessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        sign_in_button.setOnClickListener {

            val email = edit_email_login_activity.text.toString()
            Log.d("LoginActivity", "Email: $email")

            val password = edit_password_login_activity.text.toString()
            Log.d("LoginActivity", "Password: $password")
        }

    }
}
