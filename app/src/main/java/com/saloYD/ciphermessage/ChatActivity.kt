package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.alert_dialog_decrypt.view.*
import kotlinx.android.synthetic.main.alert_dialog_decrypt.view.textview_message_content_decrypt
import kotlinx.android.synthetic.main.alert_dialog_decrypted_message.view.*
import kotlinx.android.synthetic.main.alert_dialog_decrypted_message.view.decrypted_message_textview
import kotlinx.android.synthetic.main.alert_dialog_encrypt.view.*
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

        new_message_button.setOnLongClickListener {

            alertDialogEncrypt()

            return@setOnLongClickListener false
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toUri = toUser?.userImage
        val toId = toUser?.userUid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        Picasso.get().load(toUri).into(circle_username_photo_in_chat)

        reference.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {

                        val currentUser = MessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))

                        adapter.setOnItemClickListener { item, view ->

                            alertDialogDecrypt(chatMessage.text)
                        }
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))

                        adapter.setOnItemClickListener { item, view ->

                            alertDialogDecrypt(chatMessage.text)
                        }
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

    private fun alertDialogDecrypt(messageContent: String) {
        val dViewDecrypt = LayoutInflater.from(this).inflate(R.layout.alert_dialog_decrypt, null)
        val dBuilderDecrypt = AlertDialog.Builder(this)
            .setView(dViewDecrypt)

        val alertDialogDecrypt = dBuilderDecrypt.show()

        dViewDecrypt.textview_message_content_decrypt.text = messageContent

        dViewDecrypt.ok_button_key_decrypt.setOnClickListener {

            val cipherKeyDecrypt = dViewDecrypt.edittext_key_cipher_decrypt.text.toString().toInt()
            val messageDecrypted = decrypt(messageContent, cipherKeyDecrypt)

            alertDialogDecryptedMessage(messageDecrypted)

            alertDialogDecrypt.dismiss()
        }
    }

    private fun alertDialogDecryptedMessage(messageDecryptedContent: String) {
        val dViewDecryptedMessage = LayoutInflater.from(this).inflate(R.layout.alert_dialog_decrypted_message, null)
        val dBuilderDecryptedMessage = AlertDialog.Builder(this)
            .setView(dViewDecryptedMessage)

        val alertDialogDecryptedMessage = dBuilderDecryptedMessage.show()

        dViewDecryptedMessage.decrypted_message_textview.text = messageDecryptedContent

        dViewDecryptedMessage.ok_button_decrypted.setOnClickListener {

            alertDialogDecryptedMessage.dismiss()
        }
    }

    private fun alertDialogEncrypt() {
        val dViewEncrypt = LayoutInflater.from(this).inflate(R.layout.alert_dialog_encrypt, null)
        val dBuilderEncrypt = AlertDialog.Builder(this)
            .setView(dViewEncrypt)

        val alertDialogEncrypt = dBuilderEncrypt.show()

        dViewEncrypt.ok_button_key_encrypt.setOnClickListener {

            val cipherKeyEncrypt = dViewEncrypt.edittext_key_cipher_encrypt.text.toString().toInt()

            alertDialogEncrypt.dismiss()
            doEncryptMessages(cipherKeyEncrypt)
        }
    }

    private fun doEncryptMessages(cipherKey: Int) {

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        var messageText = new_message_edittext.text.toString()
        val encryptMessage = encrypt(messageText, cipherKey)
        messageText = encryptMessage

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user.userUid

        val fromReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(fromReference.key!!, messageText, fromId, toId, System.currentTimeMillis() / 1000)

        fromReference.setValue(chatMessage)
            .addOnSuccessListener {

                Log.d(TAG, "Encrypt message saved to firebase: ${fromReference.key}")
                new_message_edittext.text.clear()
                recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)
    }


    private fun doToSendMessage() {

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val messageText = new_message_edittext.text.toString()

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user.userUid

        val fromReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(fromReference.key!!, messageText, fromId, toId, System.currentTimeMillis() / 1000)

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

    private fun encrypt(s: String, key: Int): String {
        val offset = key % 26
        if (offset == 0) return s
        var d: Char
        val chars = CharArray(s.length)
        for ((index, c) in s.withIndex()) {
            if (c in 'A'..'Z') {
                d = c + offset
                if (d > 'Z') d -= 26
            }
            else if (c in 'a'..'z') {
                d = c + offset
                if (d > 'z') d -= 26
            }
            else
                d = c
            chars[index] = d
        }
        return chars.joinToString("")
    }

    private fun decrypt(s: String, key: Int): String {
        return encrypt(s, 26 - key)
    }

    fun compute(body: (foo: String) -> Unit) { body.invoke("problem solved") }

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
