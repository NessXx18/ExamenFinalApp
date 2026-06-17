package org.inovaapp.project.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import org.inovaapp.project.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    var isLoginMode by remember { mutableStateOf(true) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(MindGreenDark, MindGreen, MindBackground))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🌿", fontSize = 64.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "MindGuard",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Estás acompañado",
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MindBackground)
                    ) {
                        listOf("Iniciar sesión" to true, "Registrarse" to false)
                            .forEach { (label, value) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isLoginMode == value) MindGreen
                                            else Color.Transparent
                                        )
                                        .clickable { isLoginMode = value }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label,
                                        color = if (isLoginMode == value) Color.White
                                        else MindOnBackground,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                    }

                    Spacer(Modifier.height(20.dp))

                    if (!isLoginMode) {
                        MindTextField(
                            value = state.name,
                            onValueChange = vm::onNameChange,
                            label = "Nombre",
                            placeholder = "Tu nombre"
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    MindTextField(
                        value = state.email,
                        onValueChange = vm::onEmailChange,
                        label = "Correo",
                        placeholder = "correo@ejemplo.com",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(Modifier.height(12.dp))

                    MindTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChange,
                        label = "Contraseña",
                        placeholder = "••••••••",
                        isPassword = true
                    )

                    AnimatedVisibility(state.error != null) {
                        state.error?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, color = MindError, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { if (isLoginMode) vm.login() else vm.register() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MindGreen),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                if (isLoginMode) "Entrar" else "Crear cuenta",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MindTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = if (isPassword) {
            {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Ocultar" else "Ver", color = MindGreen)
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MindGreen,
            focusedLabelColor = MindGreen,
            cursorColor = MindGreen
        )
    )
}