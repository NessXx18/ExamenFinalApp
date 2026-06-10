package org.inovaapp.project.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import org.inovaapp.project.data.models.Message
import org.inovaapp.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    sessionId: String,
    onBack: () -> Unit,
    vm: ChatViewModel = viewModel(factory = ChatViewModelFactory(sessionId))
) {
    val state by vm.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("MindGuard", fontWeight = FontWeight.Bold)
                        Text(
                            "Tu acompañante",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
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
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.messages.isEmpty() && !state.isLoading) {
                    item { WelcomeMessage() }
                }
                items(state.messages) { message ->
                    if (message.hasRiskSignal && message.role == "assistant") {
                        RiskAlert()
                        Spacer(Modifier.height(4.dp))
                    }
                    ChatBubble(message = message)
                }
                if (state.isTyping) {
                    item { TypingIndicator() }
                }
            }

            state.error?.let {
                Text(
                    it,
                    color = MindError,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            ChatInput(
                value = state.inputText,
                onValueChange = vm::onInputChange,
                onSend = vm::sendMessage,
                isEnabled = !state.isTyping
            )
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MindGreen, shape = RoundedCornerShape(50))
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (isUser) MindGreen else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) Color.White else MindOnBackground,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MindGreen, shape = RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) { Text("🌿", fontSize = 16.sp) }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("Escribiendo...", color = Color.Gray)
        }
    }
}

@Composable
fun RiskAlert() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠️", fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    "Línea de ayuda 24/7",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "SAPTEL: 55 5259-8121",
                    color = MindGreenDark,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun WelcomeMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🌿", fontSize = 48.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Hola, soy MindGuard",
            fontWeight = FontWeight.Bold,
            color = MindOnBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Estoy aquí para escucharte.\n¿Cómo te sientes hoy?",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Escribe cómo te sientes...") },
            shape = RoundedCornerShape(24.dp),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MindGreen,
                cursorColor = MindGreen
            )
        )
        Spacer(Modifier.width(8.dp))
        FloatingActionButton(
            onClick = { if (value.isNotBlank() && isEnabled) onSend() },
            containerColor = if (value.isNotBlank() && isEnabled) MindGreen else Color.LightGray,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
        }
    }
}