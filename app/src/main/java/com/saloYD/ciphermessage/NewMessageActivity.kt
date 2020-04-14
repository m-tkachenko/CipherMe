package com.saloYD.ciphermessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.users_row_messages.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        return_to_messages_button.setOnClickListener {
            finish()
        }

        searchUsers()
    }

    companion object {

        const val USER_KEY = "USER_KEY"
    }

    private fun searchUsers(){

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()


                p0.children.forEach{

                    Log.d("NewMessageActivityTag", it.toString())
                    val user = it.getValue(User::class.java)

                    if(user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatActivity::class.java)
//                    intent.putExtra(USER_KEY,  userItem.user.username)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                recyclerview_users_messages.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.username_textview_newmessage_row.text = user.username

        Picasso.get().load(user.userImage).into(viewHolder.itemView.circle_username_photo_row)
    }

    override fun getLayout(): Int {

        return R.layout.users_row_messages
    }
}
