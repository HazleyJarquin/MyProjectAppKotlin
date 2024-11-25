package com.example.myprojectapplication

import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myprojectapplication.databinding.ActivityHomeBinding
import com.example.myprojectapplication.helpers.GoogleAuthHelper
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var googleAuthHelper: GoogleAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        googleAuthHelper = GoogleAuthHelper(this)
        googleAuthHelper.init()

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
            tvMessage.text = "Bienvenido, ${name ?: "World"}"

            btnSignOutGoogle.setOnClickListener {
                googleAuthHelper.signOut(context = this@HomeActivity, binding = binding)
            }
        }
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
