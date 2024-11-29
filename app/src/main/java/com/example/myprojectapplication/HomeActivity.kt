package com.example.myprojectapplication

import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myprojectapplication.databinding.ActivityHomeBinding
import com.example.myprojectapplication.helpers.GoogleAuthHelper
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

            val name = intent.extras?.getString("name")
            val photoUrl = intent.extras?.getString("photoUrl")

            if(photoUrl != null){
                showPhotoUrl(photoUrl)
            }
            tvMessage.text = "Bienvenido, ${name ?: "Usuario"}"

            btnSignOutGoogle.setOnClickListener {
                googleAuthHelper.signOut(context = this@HomeActivity, binding = binding)
            }

            btnViewUsers.setOnClickListener {
                val intent = Intent(this@HomeActivity, ViewUsersActivity::class.java)
                startActivity(intent)
            }

            btnViewWeather.setOnClickListener {
                val intent = Intent(this@HomeActivity, WeatherActivity::class.java)
                startActivity(intent)
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
