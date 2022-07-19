package ge.gabramishvilimavaliani.messagingapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.activity.ChatActivity
import ge.gabramishvilimavaliani.messagingapp.model.Users
import java.security.AccessController.getContext

class UserListAdapter(private val usersList: ArrayList<Users>): RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val nickName: TextView = itemView.findViewById(R.id.textView_Nickname)
        val status: TextView = itemView.findViewById(R.id.textView_Status)
        val profileImage: CircleImageView = itemView.findViewById(R.id.circle_imageView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = usersList[position]

        holder.nickName.text = currentItem.nickname
        holder.status.text = currentItem.profession
        Picasso.get().load(currentItem.profileImage).placeholder(R.drawable.avatar_image_placeholder).into(holder.profileImage)
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val uid = user!!.uid
//        if (currentItem.uid == uid){
//            holder.itemView.visibility = View.GONE
//        }

        holder.itemView.setOnClickListener {
            val chatIntent = Intent(holder.itemView.context, ChatActivity::class.java)
            chatIntent.putExtra("uid", currentItem.uid)
            chatIntent.putExtra("user_nickname", currentItem.nickname)
            chatIntent.putExtra("profession", currentItem.profession)
            chatIntent.putExtra("image", currentItem.profileImage)
            holder.itemView.context.startActivity(chatIntent)
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}