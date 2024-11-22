package com.example.myprojectapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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


            btnLogin.setOnClickListener{

                val username = txtedtxtUsername.text.toString()
                val password = txtedtxtPassword.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    runOnUiThread{
                        val builder = AlertDialog.Builder(this@LoginActivity)
                        builder.setTitle("Campos vacíos")
                        builder.setMessage("Por favor, llena todos los campos")
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.create().show()
                    }

                }
                else {
                val userToCheck = UsersEntity(
                    userName = username,
                    password = password,
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val user = myAppDb.usersDao().getUser(userToCheck.userName, userToCheck.password)
                    if (user != null) {
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.putExtras(Bundle().apply {
                            putString("name", user.name)
                        })
                        startActivity(intent)
                    } else {
                        runOnUiThread {
                            val builder = AlertDialog.Builder(this@LoginActivity)
                            builder.setTitle("Credenciales Incorrectas")
                            builder.setMessage("Usuario o contraseña incorrectos")
                            builder.setPositiveButton("Intentar nuevamente") { dialog, _ ->
                                dialog.dismiss()
                            }
                            builder.create().show()
                            txtedtxtUsername.text?.clear()
                            txtedtxtPassword.text?.clear()
                        }
                    }

                    }
                }

            }
            btnRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

        }
    }
}



