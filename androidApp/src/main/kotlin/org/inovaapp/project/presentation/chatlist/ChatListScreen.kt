package org.inovaapp.project.presentation.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import org.inovaapp.project.data.models.ChatSession
import org.inovaapp.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onSessionClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    vm: ChatListViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("MindGuard", fontWeight = FontWeight.Bold)
                        Text(
                            "Tus conversaciones",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = vm::createSession,
                containerColor = MindGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva sesión", tint = Color.White)
            }
        },
        containerColor = MindBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MindGreen
                    )
                }
                state.sessions.isEmpty() -> {
                    EmptySessionsMessage(
                        modifier = Modifier.align(Alignment.Center),
                        onCreate = vm::createSession
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.sessions) { session ->
                            SessionCard(
                                session = session,
                                onClick = { onSessionClick(session.id) }
                            )
                        }
                    }
                }
            }

            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun SessionCard(session: ChatSession, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MindBackground, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 24.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    session.title,
                    fontWeight = FontWeight.SemiBold,
                    color = MindOnBackground
                )
                if (session.lastMessage.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        session.lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${session.messageCount} mensajes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MindGreen
                )
            }
        }
    }
}

@Composable
fun EmptySessionsMessage(modifier: Modifier = Modifier, onCreate: () -> Unit) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("💬", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "No tienes conversaciones aún",
            fontWeight = FontWeight.SemiBold,
            color = MindOnBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Crea una nueva sesión para empezar",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onCreate,
            colors = ButtonDefaults.buttonColors(containerColor = MindGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Nueva conversación")
        }
    }
}