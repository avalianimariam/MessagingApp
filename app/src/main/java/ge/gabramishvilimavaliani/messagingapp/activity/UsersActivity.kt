package ge.gabramishvilimavaliani.messagingapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.database.*
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.adapter.UserListAdapter
import ge.gabramishvilimavaliani.messagingapp.model.Users
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.loadingBar
import kotlinx.android.synthetic.main.activity_users.*
import java.util.*
import kotlin.collections.ArrayList

class UsersActivity : AppCompatActivity() {
    lateinit var mDatabase: DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<Users>
    private lateinit var tempUserArrayList: ArrayList<Users>
    lateinit var usersListAdapter: UserListAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        collapsingToolbar()

        loadingBar.visibility = View.VISIBLE
        val searchPost = findViewById<androidx.appcompat.widget.SearchView>(R.id.userSearchView)
        init()
        getUsersData()
        searchFunction(searchPost)
    }

    private fun init() {
        userRecyclerview = findViewById(R.id.viewUsersRecyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        (userRecyclerview.layoutManager as LinearLayoutManager).stackFromEnd = true
        (userRecyclerview.layoutManager as LinearLayoutManager).reverseLayout = true

        userArrayList = arrayListOf<Users>()
        tempUserArrayList = arrayListOf<Users>()
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
                        if (it.nickname!!.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            textViewNoUser.visibility = View.GONE
                            tempUserArrayList.add(it)
                        } else if ((it.nickname!!.toLowerCase(Locale.getDefault()) != searchText)){
                            textViewNoUser.visibility = View.VISIBLE
                            tempUserArrayList.clear()
                        } else {
                            textViewNoUser.visibility = View.GONE
                        }
                    }
                    userRecyclerview.adapter!!.notifyDataSetChanged()
                } else {
                    tempUserArrayList.clear()
                    tempUserArrayList.addAll(userArrayList)
                }
                return false
            }

        })
    }

    private fun getUsersData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users")

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        loadingBar.visibility = View.GONE
                        val user = userSnapshot.getValue(Users::class.java)

                        userArrayList.add(user!!)
                        usersListAdapter = UserListAdapter(tempUserArrayList)
                        userRecyclerview.adapter = usersListAdapter
                    }
                    tempUserArrayList.addAll(userArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })

    }

    fun collapsingToolbar() {

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
}