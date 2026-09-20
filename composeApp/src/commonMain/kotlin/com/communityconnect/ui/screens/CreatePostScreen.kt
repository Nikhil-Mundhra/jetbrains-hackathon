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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.communityconnect.domain.model.Category
import com.communityconnect.domain.model.Location
import com.communityconnect.domain.model.PostType
import com.communityconnect.domain.model.Urgency
import com.communityconnect.presentation.viewmodel.CreatePostViewModel
import com.communityconnect.ui.components.CategoryChip
import com.communityconnect.ui.theme.Theme
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel,
    onBackClick: () -> Unit,
    onPostCreated: (com.communityconnect.domain.model.Post) -> Unit
) {
    val selectedType by remember { mutableStateOf<PostType>(PostType.REQUEST) }
    val selectedCategory by remember { mutableStateOf<Category>(Category.FOOD) }
    val selectedUrgency by remember { mutableStateOf<Urgency>(Urgency.MEDIUM) }
    val title by remember { mutableStateOf("") }
    val description by remember { mutableStateOf("") }
    val tags by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Create Post") },
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
            Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Post Type Selection
                SectionHeader(title = "What type of post?")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                TypeSelector(
                    selectedType = selectedType,
                    onTypeChange = { selectedType = it }
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
                
                // Category Selection
                SectionHeader(title = "Category")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                CategorySelector(
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it }
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
                
                // Urgency Selection
                SectionHeader(title = "Urgency")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                UrgencySelector(
                    selectedUrgency = selectedUrgency,
                    onUrgencyChange = { selectedUrgency = it }
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
                
                // Title Input
                SectionHeader(title = "Title")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    label = { Text("What's the title?") },
                    placeholder = { Text("e.g., Need help with grocery shopping") },
                    singleLine = true
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                
                // Description Input
                SectionHeader(title = "Description")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    label = { Text("Describe what you need or what you're offering") },
                    placeholder = { Text("Add details...") },
                    minLines = 4,
                    maxLines = 6
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                
                // Tags Input
                SectionHeader(title = "Tags (comma separated)")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    label = { Text("Tags") },
                    placeholder = { Text("e.g., groceries, elderly, urgent") },
                    singleLine = true
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                
                // Location (mock for now)
                SectionHeader(title = "Location")
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                LocationSelector()
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(32.dp))
                
                // Error
                error?.let { msg ->
                    ErrorBanner(message = msg)
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                }
                
                // Submit Button
                Button(
                    onClick = {
                        val tagList = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        val location = Location(
                            latitude = 37.7749,
                            longitude = -122.4194,
                            address = "Mission District, San Francisco",
                            neighborhood = "Mission District",
                            city = "San Francisco"
                        )
                        viewModel.createPost(
                            type = selectedType,
                            title = title,
                            description = description,
                            category = selectedCategory,
                            urgency = selectedUrgency,
                            location = location,
                            tags = tagList
                        ).fold(
                            success = { post ->
                                onPostCreated(post)
                            },
                            error = { /* handled by error state */ }
                        )
                    },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && description.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = androidx.compose.ui.Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Post", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun TypeSelector(selectedType: PostType, onTypeChange: (PostType) -> Unit) {
    Row(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PostType.values().forEach { type ->
            val isSelected = selectedType == type
            val (label, icon) = when (type) {
                PostType.REQUEST -> "Request" to Icons.Default.HelpOutline
                PostType.OFFER -> "Offer" to Icons.Default.Gift
                PostType.EVENT -> "Event" to Icons.Default.Event
                PostType.ANNOUNCEMENT -> "Notice" to Icons.Default.Campaign
            }
            
            Chip(
                onClick = { onTypeChange(type) },
                colors = androidx.compose.material3.ChipDefaults.chipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = androidx.compose.ui.Modifier.size(16.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(4.dp))
                    Text(text = label, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun CategorySelector(selectedCategory: Category, onCategoryChange: (Category) -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Category.values().chunked(3).forEach { chunk ->
            Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chunk.forEach { category ->
                    val isSelected = selectedCategory == category
                    Chip(
                        onClick = { onCategoryChange(category) },
                        colors = androidx.compose.material3.ChipDefaults.chipColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    ) {
                        CategoryChip(category = category)
                    }
                }
            }
        }
    }
}

@Composable
fun UrgencySelector(selectedUrgency: Urgency, onUrgencyChange: (Urgency) -> Unit) {
    Row(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Urgency.values().forEach { urgency ->
            val isSelected = selectedUrgency == urgency
            val (bgColor, textColor) = when (urgency) {
                Urgency.CRITICAL -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                Urgency.HIGH -> Color(0xFFFF9800) to Color.White
                Urgency.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
                Urgency.LOW -> MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurfaceVariant
            }
            
            Chip(
                onClick = { onUrgencyChange(urgency) },
                colors = androidx.compose.material3.ChipDefaults.chipColors(
                    containerColor = if (isSelected) bgColor else MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = if (isSelected) textColor else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = urgency.icon, style = MaterialTheme.typography.labelSmall)
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(4.dp))
                    Text(text = urgency.label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal))
                }
            }
        }
    }
}

@Composable
fun LocationSelector() {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = colors.primary
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
            Column(crossAxisAlignment = androidx.compose.ui.Alignment.Start) {
                Text("Current Location", style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
                Text("Mission District, San Francisco", style = MaterialTheme.typography.bodyMedium)
            }
            androidx.compose.ui.layout.Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
            Text("Use current", style = MaterialTheme.typography.labelMedium, color = colors.primary)
        }
    }
}

@Composable
fun ErrorBanner(message: String) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.errorContainer)
    ) {
        androidx.compose.material3.Text(
            text = message,
            color = colors.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        )
    }
}