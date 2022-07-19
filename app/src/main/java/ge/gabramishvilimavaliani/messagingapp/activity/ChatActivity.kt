package ge.gabramishvilimavaliani.messagingapp.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.adapter.MessageAdapter
import ge.gabramishvilimavaliani.messagingapp.model.Messages
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {

    private var messageReceiverID: String? = null
    private var messageReceiverProfession: String? = null
    private var messageReceiverName: String? = null
    private var messageReceiverImage: String? = null
    private var messageSenderID: String? = null
    private lateinit var messagesList: ArrayList<Messages>
    private var linearLayoutManager: LinearLayoutManager? = null
    private var messageAdapter: MessageAdapter? = null
    private var saveCurrentTime: String? = null
    private var saveCurrentDate: String? = null
    private lateinit var userNickName: String
    private lateinit var userStatus: String
    private lateinit var userProfileImage: String

    lateinit var mDatabase: DatabaseReference
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        getCurrentUserData()
        mDatabase = FirebaseDatabase.getInstance().reference
        val user = mAuth.currentUser
        val uid = user!!.uid
        initializeControllers()
        messageReceiverName = intent.extras?.get("user_nickname").toString()
        messageReceiverImage = intent.extras?.get("image").toString()
        messageReceiverProfession = intent.extras?.get("profession").toString()
        messageReceiverID = intent.extras?.get("uid").toString()
        messageSenderID = uid

        textView_Nickname.text = messageReceiverName
        textView_Status.text = messageReceiverProfession
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.avatar_image_placeholder)
            .into(circle_imageView)

        if (messageReceiverID.equals(messageSenderID)) {
            linear_chat_layout.visibility = View.GONE
        }

        btn_send_chat_message.setOnClickListener {
            sendMessage()
        }

        back_arrow.setOnClickListener {
            finish()
        }


        mDatabase.child("Messages").child(messageSenderID!!).child(messageReceiverID!!).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val messages: Messages? = dataSnapshot.getValue(Messages::class.java)
                    messagesList.add(messages!!)
                    messageAdapter!!.notifyDataSetChanged()
                    chat_message_list.smoothScrollToPosition(chat_message_list.adapter!!.itemCount)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeButtonEnabled(true)
        collapsingToolbar()

    }

    private fun getCurrentUserData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users")
        mDatabase.child(mAuth.uid.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userNickName = snapshot.child("nickname").value.toString()
                    userStatus = snapshot.child("profession").value.toString()
                    userProfileImage = snapshot.child("profileImage").value.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun collapsingToolbar() {

        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.title = " "
        val app_bar: AppBarLayout = findViewById(R.id.appbar)

        app_bar.addOnOffsetChangedListener(object :
            AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = false
                } else {
                    collapsingToolbarLayout.title = ""
                    isShow = true
                }
            }

        })
    }

    private fun initializeControllers() {

        messagesList = arrayListOf<Messages>()

        messageAdapter = MessageAdapter(messagesList)
        linearLayoutManager = LinearLayoutManager(this)
        chat_message_list.layoutManager = linearLayoutManager
        chat_message_list.adapter = messageAdapter

        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
        saveCurrentDate = currentDate.format(calendar.time)

        val currentTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        saveCurrentTime = currentTime.format(calendar.time)
    }

    private fun sendMessage() {
        val messageText = edt_chat_input_message.text.toString()
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show()
        } else {
            val messageSenderRef = "Messages/$messageSenderID/$messageReceiverID"
            val messageReceiverRef = "Messages/$messageReceiverID/$messageSenderID"
            val userMessageKeyRef: DatabaseReference =
                mDatabase.child("Messages").child(messageSenderID!!).child(messageReceiverID!!)
                    .push()
            val messagePushID = userMessageKeyRef.key
            val messageTextBody: MutableMap<String, Any?> = HashMap()
            messageTextBody["message"] = messageText
            messageTextBody["from"] = messageSenderID
            messageTextBody["to"] = messageReceiverID
            messageTextBody["messageID"] = messagePushID
            messageTextBody["time"] = saveCurrentTime
            messageTextBody["date"] = saveCurrentDate
            val messageBodyDetails: MutableMap<String, Any> = HashMap()
            messageBodyDetails["$messageSenderRef/$messagePushID"] = messageTextBody
            messageBodyDetails["$messageReceiverRef/$messagePushID"] = messageTextBody
            mDatabase.updateChildren(messageBodyDetails)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveForChat(messageText)
                        saveForChatReceiver(messageText)
                        Toast.makeText(this@ChatActivity, "Message Sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ChatActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                    edt_chat_input_message.setText("")
                }
        }
    }

    private fun saveForChat(messageText: String) {
        val chatTextBody: MutableMap<String, Any?> = HashMap()
        chatTextBody["message"] = messageText
        chatTextBody["time"] = saveCurrentTime
        chatTextBody["receiverID"] = messageReceiverID
        chatTextBody["profileImage"] = messageReceiverImage
        chatTextBody["nickname"] = messageReceiverName
        chatTextBody["profession"] = messageReceiverProfession

        mDatabase.child("Chats").child(messageSenderID!!).child(messageReceiverID!!).updateChildren(chatTextBody)

    }

     private fun saveForChatReceiver(messageText: String) {
        val chatTextBody: MutableMap<String, Any?> = HashMap()
        chatTextBody["message"] = messageText
        chatTextBody["time"] = saveCurrentTime
        chatTextBody["receiverID"] = messageSenderID
        chatTextBody["profileImage"] = userProfileImage
        chatTextBody["nickname"] = userNickName
        chatTextBody["profession"] = userStatus

        mDatabase.child("Chats").child(messageReceiverID!!).child(messageSenderID!!).updateChildren(chatTextBody)

    }


}