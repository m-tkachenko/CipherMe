package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_up_button.setOnClickListener {
            doRegister()
        }

        button_textview_account.setOnClickListener{
            Log.d("MainActivity", "Try to show LoginActivity")

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

    }

    private fun doRegister() {

        val email = edit_email.text.toString()
        Log.d("MainActivity", "Email: $email")

        val password = edit_password.text.toString()
        Log.d("MainActivity", "Password: $password")


        if(email.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "Please enter email or password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                val userUid = it.result?.user?.uid
                Log.d("MainActivity", "New user uid: $userUid")
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Fail of register: ${it.message}")
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
