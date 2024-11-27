package com.example.myprojectapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myprojectapplication.databinding.ActivityUserByIdBinding
import com.example.myprojectapplication.utils.showAlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewUserByIdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserByIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserByIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myAppDb = (application as MyAppRoomApplication).myAppDb
        val id = intent.extras?.getInt("id") ?: 0

        binding.apply {


            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }


            deleteBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        myAppDb.usersDao().deleteUserById(id)
                        val userExists = myAppDb.usersDao().getUserById(id) != null

                        runOnUiThread {
                            if (!userExists) {
                                Toast.makeText(
                                    this@ViewUserByIdActivity,
                                    "Usuario con ID $id eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(this@ViewUserByIdActivity, ViewUsersActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                showAlertDialog(
                                    "Error",
                                    "Error al eliminar el usuario con ID $id",
                                    "OK",
                                    this@ViewUserByIdActivity
                                )
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            e.printStackTrace()
                            showAlertDialog(
                                "Error",
                                "Error al eliminar el usuario con ID $id: ${e.message}",
                                "OK",
                                this@ViewUserByIdActivity
                            )
                        }
                    }
                }
            }


            updateUserBtn.setOnClickListener {
                val intent = Intent(this@ViewUserByIdActivity, UpdateUserActivity::class.java)
                intent.putExtras(Bundle().apply {
                    putInt("id", id)
                    putString("name", nameTxt.text.toString())
                    putString("email", emailTxt.text.toString())
                    putString("username", usernameTxt.text.toString())
                })
                startActivity(intent)
            }


            CoroutineScope(Dispatchers.IO).launch {
                try {

                    val user = myAppDb.usersDao().getUserById(id)


                   runOnUiThread {
                        if (user != null) {
                            nameTxt.text = user.name
                            emailTxt.text = user.email
                            usernameTxt.text = user.userName
                            toolbar.setTitle("Usuario ${user.name} con ID ${user.id}")
                        } else {
                            showAlertDialog(
                                "Error",
                                "Usuario con ID $id no encontrado",
                                "OK",
                                this@ViewUserByIdActivity
                            )
                        }
                    }
                } catch (e: Exception) {

                    withContext(Dispatchers.Main) {
                        e.printStackTrace()
                        showAlertDialog(
                            "Error",
                            "Error al cargar el usuario con ID $id: ${e.message}",
                            "OK",
                            this@ViewUserByIdActivity
                        )
                    }
                }
            }

        }
    }
}
