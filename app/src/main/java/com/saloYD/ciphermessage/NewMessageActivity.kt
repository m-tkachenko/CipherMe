package com.saloYD.ciphermessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        return_to_messages_button.setOnClickListener {
            finish()
        }

        val adapter = GroupAdapter<GroupieViewHolder>()

        recyclerview_users_messages.adapter = adapter

//        adapter.add()


    }
}

//class UserItem: Item<GroupieViewHolder>() {
//
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//
//    }
//
////    override fun getLayout(): Int {
////        R.layout.
//        return
//    }
//}
