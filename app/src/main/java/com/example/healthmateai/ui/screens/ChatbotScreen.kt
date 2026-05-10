package com.example.healthmateai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.ai.chat.ChatbotViewModel
import com.example.healthmateai.ai.chat.ChatbotViewModelFactory
import com.example.healthmateai.ui.theme.BgDark

@Composable
fun ChatbotScreen(
    contentPadding: PaddingValues,
    viewModel: ChatbotViewModel = viewModel(factory = ChatbotViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var input by remember { mutableStateOf("") }

    LaunchedEffect(uiState.messages.size, uiState.isTyping) {
        if (uiState.messages.isNotEmpty() || uiState.isTyping) {
            listState.animateScrollToItem((uiState.messages.size).coerceAtLeast(1) - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(contentPadding)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x223DE4FF)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF63EAFF),
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column {
                Text("Talk with HealthMate AI", color = Color(0xFFE8F3FF), style = MaterialTheme.typography.titleLarge)
                Text("Diet, symptoms, predictions, and wellness guidance", color = Color(0xFF9FB4DD))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isUser) 16.dp else 4.dp,
                            bottomEnd = if (message.isUser) 4.dp else 16.dp
                        ),
                        color = if (message.isUser) Color(0xFF1D4677) else Color(0xFF182947),
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            color = Color(0xFFF1F7FF)
                        )
                    }
                }
            }

            if (uiState.isTyping) {
                item {
                    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF182947)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF63EAFF)
                                )
                                Text("HealthMate AI is typing...", color = Color(0xFFBBD1F4))
                            }
                        }
                    }
                }
            }
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = Color(0xFFFFBFCB),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask AI about your health") },
                singleLine = false,
                maxLines = 4
            )
            IconButton(
                onClick = {
                    val current = input
                    input = ""
                    viewModel.send(current)
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF63EAFF))
            }
        }
    }
}
