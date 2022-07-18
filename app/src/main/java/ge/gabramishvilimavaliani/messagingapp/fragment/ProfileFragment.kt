package ge.gabramishvilimavaliani.messagingapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import ge.gabramishvilimavaliani.messagingapp.R
import ge.gabramishvilimavaliani.messagingapp.auth.LoginActivity
import ge.gabramishvilimavaliani.messagingapp.auth.SignUpActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

class ProfileFragment : Fragment() {
    lateinit var mDatabase: DatabaseReference
    var mAuth = FirebaseAuth.getInstance()
    private lateinit var userRefImages: StorageReference

    var root: ViewGroup? = null

    private val GalleryPick = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false) as ViewGroup?
        mDatabase = FirebaseDatabase.getInstance().getReference("Users")
        userRefImages = FirebaseStorage.getInstance().reference.child("ProfileImages")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
        circle_imageView.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GalleryPick)
        }
        btnUpdate.setOnClickListener {
            loading_bar.visibility = VISIBLE
            updateDatabase()
        }
        btnSignOut.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun getUserData() {
        loading_bar.visibility = VISIBLE
        mDatabase.child(mAuth.uid.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    loading_bar.visibility = GONE
                    val userNickName = snapshot.child("nickname").value.toString()
                    val userStatus = snapshot.child("status").value.toString()
                    val userProfileImage = snapshot.child("profileImage").value.toString()
                    edtNickName.setText(userNickName)
                    edtStatus.setText(userStatus)
                    Picasso.get().load(userProfileImage).placeholder(R.drawable.avatar_image_placeholder).into(circle_imageView)
                }
                if (!snapshot.exists()) {
                    loading_bar.visibility = GONE
                    edtNickName.setText("Enter your nickname")
                    edtStatus.setText("What I do")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryPick && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            imageUri = data.data!!
            circle_imageView.setImageURI(imageUri)
            loading_bar.visibility = VISIBLE
            updateImage()
        }
    }

    private fun updateImage() {
        val ref = userRefImages.child(imageUri!!.lastPathSegment + UUID.randomUUID().toString())
        ref.putFile(imageUri!!)
            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    loading_bar.visibility = GONE
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val uid = user!!.uid
                    val downloadUri = task.result
                    val profileMap: HashMap<String, Any> = HashMap()
                    profileMap["profileImage"] = downloadUri.toString()
                    mDatabase.child(uid).updateChildren(profileMap)
                        .addOnCompleteListener {
                            loading_bar.visibility = GONE
                            Toast.makeText(requireActivity(), "Profile Image updated successfully", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Handle failures
                }
            }.addOnFailureListener {

            }
    }

    private fun updateDatabase() {
        val user = mAuth.currentUser
        val uid = user!!.uid

        val profileMap: HashMap<String, Any> = HashMap()
        profileMap["nickname"] = edtNickName.text.toString().trim()
        profileMap["status"] = edtStatus.text.toString().trim()


        mDatabase.child(uid).updateChildren(profileMap)
            .addOnCompleteListener {
                loading_bar.visibility = GONE
                Toast.makeText(requireActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
    }

}