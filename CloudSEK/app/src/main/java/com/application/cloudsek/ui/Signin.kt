package com.application.cloudsek.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.application.cloudsek.MainActivity
import com.application.cloudsek.databinding.ActivitySigninBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Signin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var signinBinding: ActivitySigninBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN: Int = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signinBinding = ActivitySigninBinding.inflate(layoutInflater)

        setContentView(signinBinding.root)
        auth = Firebase.auth
        val mfireBaseUser: FirebaseUser? = auth.currentUser
        if (mfireBaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            signinBinding.GoogleLogin.setOnClickListener {
                googleSignIN()
            }
        }
    }

    private fun googleSignIN() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signinBinding.GoogleLogin.setOnClickListener {
            signIn()

        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {

        try {
            val account = completedTask.getResult(ApiException::class.java)
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                val personEmail = acct.email
                val personphoto = acct.photoUrl.toString()

                val sharedPreferences: SharedPreferences = getSharedPreferences("PersonName",
                    MODE_PRIVATE)

                val editor = sharedPreferences.edit()
                editor.putString("PersonName", personName)
                editor.apply()

                auth.createUserWithEmailAndPassword(personEmail.toString(), personEmail.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserInfo(personName, personEmail)
                        }
                    }
                Toast.makeText(this, "Welcome " + personName, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveUserInfo(personName: String?, personEmail: String?) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()

        userMap["uid"] = currentUserID
        userMap["fullName"] = personName.toString()
        userMap["Email"] = personEmail.toString()

        userRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,
                        "Account has been created successfully",
                        Toast.LENGTH_SHORT).show()


                }
            }
    }
}