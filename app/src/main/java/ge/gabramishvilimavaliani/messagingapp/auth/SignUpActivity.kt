package ge.gabramishvilimavaliani.messagingapp.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.activity.MainActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

/*For Firebase Auth and database*/
lateinit var mDatabase: DatabaseReference
var mAuth = FirebaseAuth.getInstance()
private lateinit var userRefImages: StorageReference
var user = FirebaseAuth.getInstance().currentUser

private val GalleryPick = 1
private var imageUri: Uri? = null


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mDatabase = FirebaseDatabase.getInstance().getReference("Users")
        userRefImages = FirebaseStorage.getInstance().reference.child("ProfileImages")

        circle_imageView.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GalleryPick)
        }

        btnSignUp.setOnClickListener{
            loading_bar.visibility = View.VISIBLE
            signUp()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            circle_imageView.setImageURI(imageUri)
        }
    }

    private fun saveImage() {
        val ref = userRefImages.child(imageUri!!.lastPathSegment + UUID.randomUUID().toString())
        ref.putFile(imageUri!!)
            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    saveToDatabase(downloadUri.toString())
                } else {
                    // Handle failures
                }
            }.addOnFailureListener {

            }
    }

    private fun saveToDatabase(uri: String) {
        val user = mAuth.currentUser
        val uid = user!!.uid

        val profileMap: HashMap<String, Any> = HashMap()
        profileMap["uid"] = uid
        profileMap["nickname"] = edtNickName.text.toString().trim()
        profileMap["profession"] = edtStatus.text.toString().trim()
        profileMap["profileImage"] = uri

        mDatabase.child(uid).updateChildren(profileMap)
            .addOnCompleteListener {
                loading_bar.visibility = View.GONE
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    private fun signUp() {
        val nickName = "${edtNickName.text.toString().trim()}@mail.com"
        val password = edtPassword.text.toString().trim()
        val status = edtStatus.text.toString().trim()

        if (nickName.isNotEmpty() &&
            password.isNotEmpty() &&
            status.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(nickName, password).addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    if(imageUri == null) {
                        saveToDatabase("No Image")
                    } else {
                        saveImage()
                    }

                } else {
                    loading_bar.visibility = View.GONE
                    Toast.makeText(this, "Error registering, try again later :(", Toast.LENGTH_LONG).show()
                }
            }
        }else {
            loading_bar.visibility = View.GONE
            Toast.makeText(this,"Please fill up the Credentials :|", Toast.LENGTH_LONG).show()
        }
    }
}