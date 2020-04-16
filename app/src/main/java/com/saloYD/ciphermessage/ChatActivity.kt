package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.saloYD.ciphermessage.Classes.ChatMessage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.users_row_chat_from.view.*
import kotlinx.android.synthetic.main.users_row_chat_to.view.*


class ChatActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        username_textview_chat_row.text = user.username

        return_to_messages_button.setOnClickListener {

            intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

//        setupData()

        new_message_button.setOnClickListener {

            Log.d(TAG, "Atempt to send message")
            doToSendMessage()
            listenForMessages()
        }

    }

    private fun listenForMessages() {

        val reference = FirebaseDatabase.getInstance().getReference("/messages")

        reference.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG, chatMessage?.text)
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun setupData() {

        val adapter = GroupAdapter<GroupieViewHolder>( )

        adapter.add(ChatFromItem("from message to whatever"))
        adapter.add(ChatToItem("to message from me because i want to do it for you"))

        recyclerview_chat.adapter = adapter
    }

    private fun doToSendMessage() {

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val messageText = new_message_edittext.text.toString()
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user.userUid

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(reference.key!!, messageText,  fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {

                Log.d(TAG, "Message saved to firebase: ${reference.key} ")
            }
    }
}

class ChatFromItem(val userText : String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.text_of_message_from.text = userText
    }

    override fun getLayout(): Int {
        return R.layout.users_row_chat_from
    }
}

class ChatToItem(val userText : String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.text_of_message_to.text = userText
    }

    override fun getLayout(): Int {
        return R.layout.users_row_chat_to
    }
}
