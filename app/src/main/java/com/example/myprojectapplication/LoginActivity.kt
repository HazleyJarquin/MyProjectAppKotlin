package com.example.myprojectapplication


import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myprojectapplication.database.entity.UsersEntity
import com.example.myprojectapplication.databinding.ActivityMainBinding
import com.example.myprojectapplication.helpers.GoogleAuthHelper
import com.example.myprojectapplication.utils.showAlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleAuthHelper: GoogleAuthHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
    private fun requestPermission(view: View) {
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

    private fun getLocation() {
        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permisos si no se han otorgado
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {

                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val cityName = addresses?.get(0)?.locality

                cityName?.let {
                    val intent = Intent(this@LoginActivity, WeatherActivity::class.java)
                    intent.putExtras(Bundle().apply {
                        putString("City", it)
                    })
                    startActivity(intent)
                    Log.d("Weather", "Ciudad: $it")
                } ?: println("No se pudo obtener la ciudad.")
            } else {
                println("No se pudo obtener la ubicación.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
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

