package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        sign_in_button.setOnClickListener {
            doSignIn()
            sign_in_button.isEnabled = false
            sign_in_button.isClickable = false
        }

        button_textview_back_to_reg.setOnClickListener {

            Log.d("LoginActivity", "Back to MainActivity")

            finish()
        }
    }

    private fun doSignIn() {

        val email = edit_email_login_activity.text.toString()
        Log.d("LoginActivity", "Email: $email")

        val password = edit_password_login_activity.text.toString()
        Log.d("LoginActivity", "Password: $password")

        if(email.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "Please enter email or password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                val userUid = it.result?.user?.uid
                Log.d("LoginActivity", "Not new user uid: $userUid")

                intent = Intent(this, MessagesActivity::class.java)

                // Clear our stack of activities // Very cool
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)
            }

            .addOnFailureListener {
                Log.d("LoginActivity", "Fail of sign in: ${it.message}")
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()

            }
    }
}
