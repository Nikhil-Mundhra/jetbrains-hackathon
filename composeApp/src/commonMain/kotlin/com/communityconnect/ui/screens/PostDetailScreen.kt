package com.communityconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.communityconnect.domain.model.Response
import com.communityconnect.domain.model.ResponseStatus
import com.communityconnect.domain.model.ResponseType
import com.communityconnect.presentation.viewmodel.PostDetailViewModel
import com.communityconnect.ui.components.AsyncImage
import com.communityconnect.ui.components.EmptyState
import com.communityconnect.ui.components.PostCard
import com.communityconnect.ui.theme.Theme
import kotlinx.coroutines.launch

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel,
    postId: java.util.UUID,
    onBackClick: () -> Unit
) {
    val post by viewModel.post.collectAsState()
    val responses by viewModel.responses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val responseMessage by remember { mutableStateOf("") }
    
    if (post == null) {
        LoadingState(modifier = androidx.compose.ui.Modifier.fillMaxSize())
        return
    }
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Post Details") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                // Post header
                PostCard(
                    post = post,
                    onClick = {},
                    onLikeClick = {},
                    onResponseClick = {},
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                
                // Responses section
                Column(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Responses (${responses.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                    
                    if (responses.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.ChatBubbleOutline,
                            title = "No responses yet",
                            message = "Be the first to respond to this post!",
                            modifier = androidx.compose.ui.Modifier.weight(1f)
                        )
                    } else {
                        LazyColumn(
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(responses) { response ->
                                ResponseCard(
                                    response = response,
                                    onAcceptClick = { viewModel.acceptResponse(response.id) },
                                    onDeclineClick = { /* decline */ },
                                    showActions = post.authorId == getCurrentUserId()
                                )
                            }
                        }
                    }
                }
                
                // Respond input
                RespondInput(
                    message = responseMessage,
                    onMessageChange = { responseMessage = it },
                    onSendClick = {
                        if (responseMessage.isNotBlank()) {
                            viewModel.respondToPost(responseMessage, ResponseType.CAN_HELP)
                            responseMessage = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ResponseCard(
    response: Response,
    onAcceptClick: () -> Unit,
    onDeclineClick: () -> Unit,
    showActions: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    url = response.responderAvatarUrl,
                    contentDescription = response.responderName,
                    size = 40.dp
                )
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
                Column(crossAxisAlignment = androidx.compose.ui.Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = response.responderName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
                        ResponseTypeBadge(type = response.type)
                        ResponseStatusBadge(status = response.status)
                    }
                    Text(
                        text = response.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }
            
            if (showActions && response.status == ResponseStatus.PENDING) {
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
                Row(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.end
                ) {
                    Button(
                        onClick = onDeclineClick,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceContainerHighest
                        )
                    ) {
                        Text("Decline")
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
                    Button(onClick = onAcceptClick) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
fun ResponseTypeBadge(type: ResponseType) {
    val colors = MaterialTheme.colorScheme
    val (label, bgColor) = when (type) {
        ResponseType.CAN_HELP -> "Can Help" to Green100
        ResponseType.INTERESTED -> "Interested" to Blue100
        ResponseType.NEED_MORE_INFO -> "Need Info" to Orange100
        ResponseType.OFFER_ALTERNATIVE -> "Alternative" to Purple100
    }
    
    androidx.compose.material3.Chip(
        modifier = androidx.compose.ui.Modifier.height(24.dp),
        onClick = {},
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = bgColor,
            contentColor = colors.onSurfaceVariant
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
fun ResponseStatusBadge(status: ResponseStatus) {
    val colors = MaterialTheme.colorScheme
    val (label, bgColor) = when (status) {
        ResponseStatus.PENDING -> "Pending" to Neutral200
        ResponseStatus.ACCEPTED -> "Accepted" to Green100
        ResponseStatus.DECLINED -> "Declined" to Red100
        ResponseStatus.COMPLETED -> "Completed" to Blue100
    }
    
    androidx.compose.material3.Chip(
        modifier = androidx.compose.ui.Modifier.height(24.dp),
        onClick = {},
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = bgColor,
            contentColor = colors.onSurfaceVariant
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
fun RespondInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow)
    ) {
        Row(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 8.dp),
                label = { Text("Write a response...") },
                singleLine = true,
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = colors.surface,
                    focusedContainerColor = colors.surface
                )
            )
            androidx.compose.material3.IconButton(onClick = onSendClick) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = colors.primary)
            }
        }
    }
}

@Composable
fun LoadingState(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = androidx.compose.ui.Modifier.align(Alignment.Center)
        )
    }
}

private fun getCurrentUserId(): java.util.UUID {
    return java.util.UUID.fromString("11111111-1111-1111-1111-111111111111")
}