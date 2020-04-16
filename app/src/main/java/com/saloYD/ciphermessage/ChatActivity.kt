package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.users_row_chat_from.view.*
import kotlinx.android.synthetic.main.users_row_chat_to.view.*


class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY )

        username_textview_chat_row.text = user.username

        return_to_messages_button.setOnClickListener {

            intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        setupData()

    }

    private fun setupData() {

        val adapter = GroupAdapter<GroupieViewHolder>( )

        adapter.add(ChatFromItem("from message to whatever"))
        adapter.add(ChatToItem("to message from me because i want to do it for you"))

        recyclerview_chat.adapter = adapter
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
