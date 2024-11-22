package com.example.myprojectapplication

import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myprojectapplication.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.apply {
            btnNav.setOnClickListener {
                val intent = Intent(this@SecondActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            val name = intent.extras?.getString("name")
            tvMessage.text = "Hello, ${name ?: "World"}"

        }
    }
}
