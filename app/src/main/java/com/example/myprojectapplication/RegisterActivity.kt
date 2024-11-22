package com.example.myprojectapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myprojectapplication.database.entity.UsersEntity
import com.example.myprojectapplication.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val myAppDb = (application as MyAppRoomApplication).myAppDb
        binding.apply {
            btnBack.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnRegister.setOnClickListener{
                CoroutineScope(Dispatchers.IO).launch {
                    val name = etName.text.toString()
                    val email = etEmail.text.toString()
                    val username = etUsername.text.toString()
                    val password = etPassword.text.toString()

                    if (name.isNotEmpty() && email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                        val usersToInsert = UsersEntity(
                            name = name,
                            email = email,
                            userName = username,
                            password = password
                        )

                        try {

                            myAppDb.usersDao().insert(usersToInsert)

                            runOnUiThread {
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                val builder = AlertDialog.Builder(this@RegisterActivity)
                                builder.setTitle("Error")
                                builder.setMessage("Hubo un error al registrar el usuario. Intenta nuevamente.")
                                builder.setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                builder.create().show()
                            }
                        }
                    } else {

                        runOnUiThread {
                            val builder = AlertDialog.Builder(this@RegisterActivity)
                            builder.setTitle("Error")
                            builder.setMessage("Por favor llena todos los campos")
                            builder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            builder.create().show()
                        }
                    }
                }

            }




        }
    }
}