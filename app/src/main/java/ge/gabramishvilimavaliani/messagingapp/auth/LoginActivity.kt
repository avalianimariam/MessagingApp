package ge.gabramishvilimavaliani.messagingapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.activity.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btnSignUp
import kotlinx.android.synthetic.main.activity_login.edtNickName
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity() {
    var mAuth = FirebaseAuth.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        btnSignIn.setOnClickListener {
            loadingBar.visibility = View.VISIBLE
            login()
        }
    }

    private fun login() {
        var Email = edtNickName.text.toString().trim()
        var Password = edtPassword.text.toString().trim()

        if (Email.isNotEmpty() && Password.isNotEmpty()) {
            this.mAuth.signInWithEmailAndPassword("${edtNickName.text.toString().trim()}@mail.com", Password).addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    loadingBar.visibility = View.GONE
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    loadingBar.visibility = View.GONE
                    val message = task.exception.toString()
                    Toast.makeText(
                        this,
                        "Error: $message", Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            Toast.makeText(this, "Please fill up the Credentials :|", Toast.LENGTH_SHORT).show()
        }
    }
}