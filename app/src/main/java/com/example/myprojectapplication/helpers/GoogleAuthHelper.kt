package com.example.myprojectapplication.helpers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.viewbinding.ViewBinding
import com.example.myprojectapplication.HomeActivity
import com.example.myprojectapplication.LoginActivity
import com.example.myprojectapplication.R
import com.example.myprojectapplication.databinding.ActivityHomeBinding
import com.example.myprojectapplication.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class GoogleAuthHelper(private val context: Context) {

    private lateinit var auth: FirebaseAuth
    private var onTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest


    fun init() {
        auth = FirebaseAuth.getInstance()
        onTapClient = Identity.getSignInClient(context)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    fun signIn(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        onTapClient?.beginSignIn(signInRequest)
            ?.addOnSuccessListener { result ->
                try {
                    launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                } catch (e: Exception) {
                    Log.e("GoogleAuthHelper", "Error launching sign-in flow", e)
                }
            }
            ?.addOnFailureListener { e ->
                Log.e("GoogleAuthHelper", "Inicio de sesion fallido", e)
            }
    }

    fun handleSignInResult(data: Intent?) {
        val task = onTapClient?.getSignInCredentialFromIntent(data)
        try {
            val idToken = task?.googleIdToken
            if (idToken != null) {
                authenticateWithFirebase(idToken)
            } else {
                Log.e("GoogleAuthHelper", "Google ID token is null")
            }
        } catch (e: Exception) {
            Log.e("GoogleAuthHelper", "Error handling sign-in result", e)
        }
    }

    private fun authenticateWithFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.putExtras(Bundle().apply {
                            putString("name", user.displayName)
                            putString("photoUrl", user.photoUrl.toString())
                        })
                        context.startActivity(intent)
                    }
                } else {

                    Toast.makeText(context, "Autenticacion fallida.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signOut(context: Context, binding: ActivityHomeBinding) {
        Firebase.auth.signOut()
        onTapClient?.signOut()
            ?.addOnCompleteListener {
                Toast.makeText(context, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { e ->
                Log.e("GoogleAuthHelper", "Error al cerrar sesión", e)
            }
        val intent = Intent(context, LoginActivity::class.java)
        binding.profileImage.setImageBitmap(null)
        context.startActivity(intent)
    }




    fun onStart() {
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtras(Bundle().apply {
                putString("name", user.displayName)
                putString("photoUrl", user.photoUrl.toString())
            })
            context.startActivity(intent)
        }
    }
}
