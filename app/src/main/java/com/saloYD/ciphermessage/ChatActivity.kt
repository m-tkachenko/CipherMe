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
import com.saloYD.ciphermessage.Classes.User
import com.squareup.picasso.Picasso
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

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerview_chat.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        username_textview_chat_row.text = toUser?.username

        return_to_messages_button.setOnClickListener {

            intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        listenForMessages()

        new_message_button.setOnClickListener {

            Log.d(TAG, "Atempt to send message")
            doToSendMessage()
        }
    }

    private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.userUid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        reference.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {

                        val currentUser = MessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }
                    else {

                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun doToSendMessage() {

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val messageText = new_message_edittext.text.toString()
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user.userUid

        val fromReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(fromReference.key!!, messageText,  fromId, toId, System.currentTimeMillis() / 1000)

        fromReference.setValue(chatMessage)
            .addOnSuccessListener {

                Log.d(TAG, "Message saved to firebase: ${fromReference.key}")
                new_message_edittext.text.clear()
                recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}


class ChatFromItem(val userText : String, val user : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.text_of_message_from.text = userText

        val uri = user.userImage
        val imageView = viewHolder.itemView.circle_username_photo_row_chat_from
        Picasso.get().load(uri).into(imageView)
    }

    override fun getLayout(): Int {
        return R.layout.users_row_chat_from
    }
}

class ChatToItem(val userText : String , val user : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.text_of_message_to.text = userText

        val uri = user.userImage
        val imageView = viewHolder.itemView.circle_username_photo_row_chat_to
        Picasso.get().load(uri).into(imageView)
    }

    override fun getLayout(): Int {
        return R.layout.users_row_chat_to
    }
}
