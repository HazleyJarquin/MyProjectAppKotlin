package com.example.myprojectapplication

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // Enviar el token al servidor
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Este método se ejecuta cuando se recibe una notificación push
        if (remoteMessage.data.isNotEmpty()) {
            // Maneja los datos de la notificación
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Si la notificación contiene un mensaje de notificación (visual)
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun sendRegistrationToServer(token: String?) {
        // Implementa tu lógica para enviar el token al servidor
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
