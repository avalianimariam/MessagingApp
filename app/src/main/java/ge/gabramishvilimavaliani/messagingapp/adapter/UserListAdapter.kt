package ge.gabramishvilimavaliani.messagingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.model.Users

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
        holder.status.text = currentItem.status
        Picasso.get().load(currentItem.profileImage).placeholder(R.drawable.avatar_image_placeholder).into(holder.profileImage)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}