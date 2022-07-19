package ge.gabramishvilimavaliani.messagingapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.model.Messages

class MessageAdapter(private val userMessageList: ArrayList<Messages>): RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    private var mAuth = FirebaseAuth.getInstance()
    lateinit var mUserRef: DatabaseReference
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val senderMessageText: TextView = itemView.findViewById(R.id.txt_sender_message_text)
        val receiverMessageText: TextView = itemView.findViewById(R.id.txt_receiver_message_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val messageSenderId: String = mAuth.currentUser!!.uid
        val messages = userMessageList[position]

        val fromUserID: String = messages.from!!

        mUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(fromUserID)

        holder.receiverMessageText.visibility = View.GONE
        holder.senderMessageText.visibility = View.GONE

        if (fromUserID == messageSenderId) {
            holder.senderMessageText.visibility = View.VISIBLE
            holder.senderMessageText.setBackgroundResource(R.drawable.sender_bg)
            holder.senderMessageText.setTextColor(Color.BLACK)
            holder.senderMessageText.text = messages.message.toString() + "\n \n" + messages.time + "  " + "  " + messages.date
        } else {
            holder.receiverMessageText.visibility = View.VISIBLE
            holder.receiverMessageText.setBackgroundResource(R.drawable.reciever_bg)
            holder.receiverMessageText.setTextColor(Color.BLACK)
            holder.receiverMessageText.text = messages.message.toString() + "\n \n" + messages.time + "  " + "  " + messages.date
        }

    }

    override fun getItemCount(): Int {
        return userMessageList.size
    }
}