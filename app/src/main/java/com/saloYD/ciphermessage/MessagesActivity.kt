package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saloYD.ciphermessage.Classes.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser : User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        findCurrentUser()

        Log.d("MessagesActivity", "Here in messages")
        checkUserIsLoged()

        setupUserRows()

        new_message_button_activity.setOnClickListener { userNewMessage() }
        sign_out_button.setOnClickListener { userSignOut() }
    }

    class LatestMessageRow: Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.latest_messages_row
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        }
    }

    private fun setupUserRows() {
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())

        recyclerview_latest_messages.adapter = adapter
    }

    private fun findCurrentUser() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                currentUser = p0.getValue(User::class.java)
                Log.d("MessagesActivity", "Current user: ${currentUser?.userImage}")
            }
        })
    }

    private fun checkUserIsLoged() {

        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun userSignOut() {

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun userNewMessage() {

        val intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
    }
}
