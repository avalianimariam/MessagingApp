package ge.gabramishvilimavaliani.messagingapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.adapter.UserChatAdapter
import ge.gabramishvilimavaliani.messagingapp.model.Chats
import ge.gabramishvilimavaliani.messagingapp.model.Users
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.circle_imageView
import kotlinx.android.synthetic.main.activity_login.edtNickName
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*


class ChatFragment : Fragment() {

    var root: ViewGroup? = null
    lateinit var mDatabase: DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<Chats>
    private lateinit var tempUserArrayList: ArrayList<Chats>
    lateinit var usersListAdapter: UserChatAdapter
    var mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chat, container, false) as ViewGroup?

        val activity = activity as AppCompatActivity?
        val toolbar: Toolbar = root!!.findViewById(ge.gabramishvilimavaliani.messagingapp.R.id.toolbar)
        activity!!.setSupportActionBar(toolbar)

        activity.supportActionBar!!.setHomeButtonEnabled(true)
        collapsingToolbar()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchPost = root!!.findViewById<androidx.appcompat.widget.SearchView>(R.id.userSearchView)
        init()
        getUsersData()
        searchFunction(searchPost)
    }

    fun collapsingToolbar() {

        val collapsingToolbarLayout = root!!.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        val app_bar: AppBarLayout = root!!.findViewById(R.id.appbar)

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

    private fun init() {
        userRecyclerview = root!!.findViewById(R.id.viewChatRecyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        userRecyclerview.setHasFixedSize(true)
        userRecyclerview.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, true)
        (userRecyclerview.layoutManager as LinearLayoutManager).stackFromEnd = true
        (userRecyclerview.layoutManager as LinearLayoutManager).reverseLayout = true

        userArrayList = arrayListOf<Chats>()
        tempUserArrayList = arrayListOf<Chats>()
    }

    private fun searchFunction(searchID: androidx.appcompat.widget.SearchView?) {

        searchID!!.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchID.clearFocus()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                tempUserArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    userArrayList.forEach {
                        if (it.nickname != null){
                            if (it.nickname!!.toLowerCase(Locale.getDefault()).contains(searchText)) {
                                tempUserArrayList.add(it)
                            }
                        }

                    }
                    userRecyclerview.adapter!!.notifyDataSetChanged()
                }
                else {
                    tempUserArrayList.clear()
                    tempUserArrayList.addAll(userArrayList)
                }
                return false
            }

        })
    }

    private fun getUsersData() {
        val user = mAuth.currentUser
        val uid = user!!.uid
        mDatabase = FirebaseDatabase.getInstance().getReference("Chats").child(uid)

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    userArrayList.clear()
                    tempUserArrayList.clear()
                    for (userSnapshot in snapshot.children){
                        val users = userSnapshot.getValue(Chats::class.java)
                        userArrayList.add(users!!)
                        usersListAdapter = UserChatAdapter(tempUserArrayList)
                        userRecyclerview.adapter = usersListAdapter
                    }
                    tempUserArrayList.addAll(userArrayList)
                }else{
                    userRecyclerview.adapter = UserChatAdapter(tempUserArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })

    }



}