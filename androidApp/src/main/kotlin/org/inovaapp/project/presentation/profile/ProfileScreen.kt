package org.inovaapp.project.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.inovaapp.project.data.api.ApiClient
import org.inovaapp.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MindGreen,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MindBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MindGreen, shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 48.sp)
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Usuario MindGuard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MindOnBackground
            )
            Spacer(Modifier.height(32.dp))

            InfoCard(title = "Acerca de MindGuard", content =
                "MindGuard es tu compañero de bienestar mental. " +
                        "Estamos aquí para escucharte y apoyarte en todo momento."
            )

            Spacer(Modifier.height(16.dp))

            InfoCard(title = "Línea de crisis 24/7", content = "SAPTEL: 55 5259-8121")

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    ApiClient.token = null
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MindError)
            ) {
                Text("Cerrar sesión", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = MindGreen)
            Spacer(Modifier.height(8.dp))
            Text(content, color = MindOnBackground, style = MaterialTheme.typography.bodyMedium)
        }
    }
}