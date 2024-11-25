package com.example.myprojectapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.myprojectapplication.database.entity.UsersEntity
import com.example.myprojectapplication.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Executors



class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var onTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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


        val myAppDb = (application as MyAppRoomApplication).myAppDb
        binding.apply {




                btnSignGoogle.setOnClickListener {
                    singInGoogle()
                }


                btnLogin.setOnClickListener {

                    val username = txtedtxtUsername.text.toString()
                    val password = txtedtxtPassword.text.toString()

                    if (username.isEmpty() || password.isEmpty()) {
                        runOnUiThread {
                            showAlertDialog(
                                "Campos vacíos",
                                "Por favor, llena todos los campos",
                                "OK"
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
                            }
                            else {
                                runOnUiThread {
                                    showAlertDialog(
                                        "Credenciales Incorrectas",
                                        "Usuario o contraseña incorrectos",
                                        "Intentar nuevamente"
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

    private fun singInGoogle() {
        CoroutineScope(Dispatchers.Main).launch {
            signInGoogle()
        }
    }


    private fun showAlertDialog(title: String, message: String, btnMessage: String) {
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(btnMessage) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


    private suspend fun signInGoogle() {
        val result = onTapClient?.beginSignIn(signInRequest)?.await()
        val intentSenderRequest = IntentSenderRequest.Builder(result!!.pendingIntent).build()
        activityResultLauncher.launch(intentSenderRequest)
    }


    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
                result ->
            if (
                result.resultCode == RESULT_OK
            ){
                try {
                    val credential = onTapClient!!.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener{
                            if(it.isSuccessful) {
                                val user = auth.currentUser
                                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.putExtras(Bundle().apply {
                                    putString("name", user?.displayName)
                                    putString("photoUrl", user?.photoUrl.toString())
                                })
                                startActivity(intent)

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                }
            }
        }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtras(Bundle().apply {
                putString("name", user.displayName)
                putString("photoUrl", user.photoUrl.toString())
            })
            startActivity(intent)

        }
    }
}









