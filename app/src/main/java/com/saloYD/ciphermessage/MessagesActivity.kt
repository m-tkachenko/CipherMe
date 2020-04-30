package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.saloYD.ciphermessage.Classes.ChatMessage
import com.saloYD.ciphermessage.Classes.LatestMessageRow
import com.saloYD.ciphermessage.Classes.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.alert_dialog_creators.view.*

class MessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser : User? = null
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        recyclerview_latest_messages.adapter = adapter

        findCurrentUser()

        Log.d("MessagesActivity", "Here in messages")
        checkUserIsLoged()

        adapter.setOnItemClickListener{ item, view ->
            val intent = Intent(this, ChatActivity::class.java)
            val row = item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessages()

        new_message_button_activity.setOnClickListener { userNewMessage() }
        sign_out_button.setOnClickListener { userSignOut() }
        creators_button.setOnClickListener{ creatorsButton() }
        creators_button.setOnLongClickListener {
            creatorsButtonEgg()
            return@setOnLongClickListener false
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessages = p0.getValue(ChatMessage::class.java) ?: return
                adapter.add(LatestMessageRow(chatMessages))
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
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

    private fun creatorsButton() {
        val dViewCreator = LayoutInflater.from(this).inflate(R.layout.alert_dialog_creators, null)
        val dBuilderCreator = AlertDialog.Builder(this)
            .setView(dViewCreator)

        val alertDialogCreator = dBuilderCreator.show()

        dViewCreator.ok_button_creators.setOnClickListener { alertDialogCreator.dismiss() }
    }

    private fun creatorsButtonEgg() {

        val dViewCreator = LayoutInflater.from(this).inflate(R.layout.alert_dialog_creators, null)
        val dBuilderCreator = AlertDialog.Builder(this)
            .setView(dViewCreator)

        val alertDialogCreator = dBuilderCreator.show()

        dViewCreator.creators_string.text = "God loves you!"

        dViewCreator.ok_button_creators.setOnClickListener { alertDialogCreator.dismiss() }
    }
}
