package com.example.myprojectapplication


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myprojectapplication.database.entity.UsersEntity
import com.example.myprojectapplication.databinding.ActivityMainBinding
import com.example.myprojectapplication.helpers.GoogleAuthHelper
import com.example.myprojectapplication.utils.showAlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleAuthHelper: GoogleAuthHelper

    private val signInLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                googleAuthHelper.handleSignInResult(result.data)
            } else {
                Toast.makeText(this, "Inicio de sesion fallido", Toast.LENGTH_SHORT).show()
            }
        }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Firebase Logs", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

            })

        } else {

        }
    }
    fun requestPermission(view: View) {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Firebase Logs", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.w("Firebase Logs", "Fetching FCM registration token: $token")

            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        googleAuthHelper = GoogleAuthHelper(this)
        googleAuthHelper.init()

        setContentView(binding.root)

        val myAppDb = (application as MyAppRoomApplication).myAppDb
        binding.apply {
            btnSignGoogle.setOnClickListener {
                googleAuthHelper.signIn(signInLauncher)
            }




            btnLogin.setOnClickListener {
                val username = txtedtxtUsername.text.toString()
                val password = txtedtxtPassword.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    runOnUiThread {
                        showAlertDialog(
                            "Campos vacíos",
                            "Por favor, llena todos los campos",
                            "OK",
                            this@LoginActivity
                        )
                    }
                } else {
                    val userToCheck = UsersEntity(
                        userName = username,
                        password = password,
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val user = myAppDb.usersDao()
                            .getUser(userToCheck.userName, userToCheck.password)
                        if (user != null) {
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.putExtras(Bundle().apply {
                                putString("name", user.userName)
                            })
                            startActivity(intent)
                        } else {
                            runOnUiThread {
                                showAlertDialog(
                                    "Credenciales Incorrectas",
                                    "Usuario o contraseña incorrectos",
                                    "Intentar nuevamente",
                                    this@LoginActivity
                                )
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



    override fun onStart() {
        super.onStart()
        googleAuthHelper.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(binding.root)
        }
    }
}

