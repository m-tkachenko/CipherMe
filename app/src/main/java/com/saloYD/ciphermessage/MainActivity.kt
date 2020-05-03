@file:Suppress("DEPRECATION")

package com.saloYD.ciphermessage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.saloYD.ciphermessage.Classes.User

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_up_button.setOnClickListener {
            doRegister()
            sign_up_button.isEnabled = false
            sign_up_button.isClickable = false
        }

        button_textview_account.setOnClickListener{
            Log.d("MainActivity", "Show LoginActivity")

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

        select_photo_button.setOnClickListener {
            Log.d("MainActivity", "Show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("MainActivity", "Photo is selected")
            
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            select_photo_image_view.setImageBitmap(bitmap)
            select_photo_button.alpha = 0f

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

                uploadImageToFirebase()
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Fail of register: ${it.message}")
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d("MainActivity", "Added image to storage: ${taskSnapshot.metadata?.path}" )

                ref.downloadUrl.addOnSuccessListener {
                    val urlFile = it.toString()
                    Log.d("MainActivity", "File located in: $urlFile")

                    saveUserInfoToFirebase(urlFile)
                }
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Image is not added to storage")
            }
    }

    private fun saveUserInfoToFirebase(userImage: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid,  edit_username.text.toString(), userImage)

        ref.setValue(user)
            .addOnSuccessListener {

                Log.d("MainActivity", "Saved info about user to database")
                
                intent = Intent(this, MessagesActivity::class.java)

                // Clear our stack of activities // Very cool
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Not saved info about user to database")
            }
    }
}
