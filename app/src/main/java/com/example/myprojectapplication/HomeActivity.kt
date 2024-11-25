package com.example.myprojectapplication

import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myprojectapplication.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private var onTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth
        onTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        binding.apply {
            btnAddUser.setOnClickListener {
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            val name = intent.extras?.getString("name")
            val photoUrl = intent.extras?.getString("photoUrl")

            if(photoUrl != null){
                showPhotoUrl(photoUrl)
            }
            tvMessage.text = "Hello, ${name ?: "World"}"

            btnSignOutGoogle.setOnClickListener {
                signOutUser()
            }
        }
    }



    private fun signOutUser(){
        Firebase.auth.signOut()
        Toast.makeText(this, "Usuario deslogueado", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        binding.profileImage.setImageBitmap(null)
        startActivity(intent)
    }

    private fun showPhotoUrl(photoUrl: String) {
        var image: Bitmap? = null
        val imageUrl = photoUrl ?: return
        val executorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {
                val `in` = java.net.URL(imageUrl).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            runOnUiThread {
                try {
                    Thread.sleep(1000)
                    binding.profileImage.setImageBitmap(image)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
