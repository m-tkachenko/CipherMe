package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*


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

        val adapter = GroupAdapter<GroupieViewHolder>( )

        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())

        recyclerview_chat.adapter = adapter
    }
}

class ChatFromItem : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.users_row_chat_from
    }
}

class ChatToItem : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.users_row_chat_to
    }
}
