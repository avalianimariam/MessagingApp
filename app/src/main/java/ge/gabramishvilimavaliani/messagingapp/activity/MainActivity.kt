package ge.gabramishvilimavaliani.messagingapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.BaseOnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import ge.gabramishvilimavaliani.messagingapp.auth.LoginActivity
import ge.gabramishvilimavaliani.messagingapp.auth.mAuth
import ge.gabramishvilimavaliani.messagingapp.fragment.ChatFragment
import ge.gabramishvilimavaliani.messagingapp.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ge.gabramishvilimavaliani.messagingapp.R.layout.activity_main)

        bottomNavView.background = null

        fab.setOnClickListener {
            //showing a toast message when clicked
            startActivity(Intent(this, UsersActivity::class.java))

//            Toast.makeText(this, "FloatingActionButton Clicked", Toast.LENGTH_SHORT).show()
        }

        bottomNavBar()
        checkUser()
    }

    private fun checkUser() {
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            // No user is signed in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                ge.gabramishvilimavaliani.messagingapp.R.id.bnbMenuHome -> {
                    MoveToFragment(ChatFragment())
                    return@OnNavigationItemSelectedListener true
                }
                ge.gabramishvilimavaliani.messagingapp.R.id.bnbMenuProfile -> {
                    MoveToFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }

            false
        }


    private fun bottomNavBar() {
        val navView: BottomNavigationView =
            findViewById(ge.gabramishvilimavaliani.messagingapp.R.id.bottomNavView)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        MoveToFragment(ChatFragment())
    }

    private fun MoveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(
            ge.gabramishvilimavaliani.messagingapp.R.id.fragment_container,
            fragment
        )
        fragmentTrans.commit()
    }

}