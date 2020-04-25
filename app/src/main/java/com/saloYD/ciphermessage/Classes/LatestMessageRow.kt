package com.saloYD.ciphermessage.Classes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saloYD.ciphermessage.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>() {
    var chatPartnerUser : User? = null

    override fun getLayout(): Int { return R.layout.latest_messages_row }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val charPartnerId : String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            charPartnerId = chatMessage.toId
        }
        else {
            charPartnerId = chatMessage.fromId 
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$charPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.latest_messages_username_textview.text = chatPartnerUser?.username

                Picasso.get().load(chatPartnerUser?.userImage).into(viewHolder.itemView.circle_photo_message)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}