package com.example.myprojectapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myprojectapplication.database.entity.UsersEntity
import com.example.myprojectapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()


        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val myAppDb = (application as MyAppRoomApplication).myAppDb
        binding.apply {
            btnNav.setOnClickListener {
                val intent = Intent(this@LoginActivity, SecondActivity::class.java)
                startActivity(intent)
            }






//        btnLogin.setOnClickListener{
//            val userAdd = UsersEntity(
//                name = "admin",
//                email = "admin@admin.com",
//                userName = "admin",
//                password = "admin"
//
//            )
//            CoroutineScope(Dispatchers.IO).launch {
//                myAppDb.usersDao().insert(userAdd)
//            }
//        }

            btnLogin.setOnClickListener{
                val userToCheck = UsersEntity(
                    userName = txtedtxtUsername.text.toString(),
                    password = txtedtxtPassword.text.toString(),
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val user = myAppDb.usersDao().getUser(userToCheck.userName, userToCheck.password)
                    if (user != null) {
                        val intent = Intent(this@LoginActivity, SecondActivity::class.java)
                        intent.putExtras(Bundle().apply {
                            putString("name", user.name)
                        })
                        startActivity(intent)
                    } else {
                        Log.d("LoginActivity", "User is not logged in")
                    }
                }
            }
            btnRegister.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {

                    val customers = myAppDb.usersDao().getAllUsers()

                    runOnUiThread {

                        val customerNames = customers.joinToString("\n") { it.name.toString() }

                        binding.txtViewCustomers.text = customerNames
                    }
                }
            }

        }
    }
}



