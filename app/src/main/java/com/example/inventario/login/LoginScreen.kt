package com.example.inventario.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "LOGIN_INVENTARIO"

@Composable
fun LoginScreen(onLoginSuccess: (nombreUsuario: String, emailUsuario: String) -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // ⚠️ ATENCIÓN INGENIERO NAPOLEÓN:
    // Aquí deben poner el Web Client ID de su nuevo proyecto de Firebase
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("183490095306-k148atj211rb75lgfgn1c0ogr1c0470d.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account?.idToken != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { firebaseTask ->
                        if (firebaseTask.isSuccessful) {
                            val user = auth.currentUser
                            // Capturamos el nombre real de la cuenta de Google (o el email si no tiene nombre)
                            val nombre = user?.displayName ?: "Usuario Desconocido"
                            val email = user?.email.orEmpty()
                            Log.d(TAG, "Ingreso exitoso: $nombre ($email)")

                            // Enviamos estos datos al mapa de navegación
                            onLoginSuccess(nombre, email)
                        }
                    }
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Error de Google SignIn: Código ${e.statusCode}")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Control de Inventario",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Técnica en Sistemas",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(80.dp))

        Button(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Ingresar con Google", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Acceso restringido a personal autorizado",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}