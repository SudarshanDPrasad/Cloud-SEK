package com.application.cloudsek.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.application.cloudsek.MainActivity
import com.application.cloudsek.databinding.ActivityAddPostBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {

    private lateinit var addPostBinding: ActivityAddPostBinding
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPostBinding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(addPostBinding.root)

        storagePicRef = FirebaseStorage.getInstance().reference.child("Post Pictures ")

        addPostBinding.btnSavePost.setOnClickListener {
            uploadImage()
        }

        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this@AddPostActivity)
    }

    private fun uploadImage() {
        if (imageUri == null) {
            Toast.makeText(this, "Please Select the Image", Toast.LENGTH_SHORT).show()
        } else {

            val progressDiagloue = ProgressDialog(this)
            progressDiagloue.setTitle("Adding New Post")
            progressDiagloue.setMessage("Please Wait , We are adding the Image ")
            progressDiagloue.show()

            val fileRef = storagePicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (task.isSuccessful) {
                    task.exception?.let {
                        throw it
                        progressDiagloue.dismiss()
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener(OnCompleteListener<Uri> { task->
                if(task.isSuccessful){
                    val downloadUri = task.result
                    myUrl = downloadUri.toString()
                    val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                    val postId = ref.push().key

                    val sharedPreferences : SharedPreferences = getSharedPreferences("PersonName",
                        MODE_PRIVATE)
                    val name  = sharedPreferences.getString("PersonName","")

                    val postMap = HashMap<String,Any>()
                    postMap["postid"] = postId!!
                    postMap["postImage"] = myUrl
                    postMap["publisherName"] = name!!

                    ref.child(postId).updateChildren(postMap)

                    Toast.makeText(this, "Post Uploaded Successfully ", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)

                    progressDiagloue.dismiss()

                }else{
                    progressDiagloue.dismiss()
                }
            })

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            addPostBinding.ImageUploader.setImageURI(imageUri)
        }
    }
}