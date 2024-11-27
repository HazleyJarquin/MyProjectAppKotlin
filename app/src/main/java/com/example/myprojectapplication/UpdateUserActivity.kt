package com.example.myprojectapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myprojectapplication.databinding.ActivityUpdateUserBinding
import com.example.myprojectapplication.utils.showAlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myAppDb = (application as MyAppRoomApplication).myAppDb
        val id = intent.extras?.getInt("id") ?: 0
        val name = intent.extras?.getString("name") ?: ""
        val username = intent.extras?.getString("username") ?: ""
        val email = intent.extras?.getString("email") ?: ""

        binding.apply {
            etName.setText(name)
            etUsername.setText(username)
            etEmail.setText(email)

            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnUpdate.setOnClickListener {
                val name = etName.text.toString()
                val username = etUsername.text.toString()
                val email = etEmail.text.toString()

                if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
                    runOnUiThread{
                        showAlertDialog(
                        "Campos vacíos",
                        "Por favor, llena todos los campos",
                        "OK",
                        this@UpdateUserActivity
                        )
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        myAppDb.usersDao().updateUser(id, name, username, email)
                        runOnUiThread {
                           Toast.makeText(
                                this@UpdateUserActivity,
                                "Usuario actualizado",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@UpdateUserActivity, ViewUsersActivity::class.java)
                            startActivity(intent)
                        }
                        finish()
                    }
                }
            }
        }
    }
}