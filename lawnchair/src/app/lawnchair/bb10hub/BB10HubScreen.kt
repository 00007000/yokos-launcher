package app.lawnchair.bb10hub

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BB10HubScreen(
    repository: HubRepository,
    onNotificationDismiss: (String) -> Unit,
    onNotificationTap: (String) -> Unit,
    onSnooze: (String, Long) -> Unit
) {
    val notifications by repository.notifications.collectAsStateWithLifecycle()
    val settings by repository.settings.collectAsStateWithLifecycle()
    
    var selectedCategory by remember { mutableStateOf<NotificationCategory?>(null) }
    
    val filteredNotifications = notifications.filter { notif ->
        (selectedCategory == null || notif.category == selectedCategory) &&
        notif.category !in settings.hiddenCategories
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Category filter tabs
            CategoryFilterTabs(
                categories = NotificationCategory.values().toList(),
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = if (it == selectedCategory) null else it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            
            Divider(color = Color(0xFF333333), thickness = 1.dp)
            
            // Notification list
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No notifications",
                        color = Color(0xFF999999),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredNotifications, key = { it.key }) { notif ->
                        NotificationItem(
                            notification = notif,
                            onTap = { onNotificationTap(notif.key) },
                            onDismiss = { onNotificationDismiss(notif.key) },
                            onSnooze = { durationMs -> onSnooze(notif.key, durationMs) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterTabs(
    categories: List<NotificationCategory>,
    selectedCategory: NotificationCategory?,
    onCategorySelected: (NotificationCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = categories.indexOfFirst { it == selectedCategory },
        modifier = modifier,
        containerColor = Color(0xFF1A1A1A),
        contentColor = Color.White,
        edgePadding = 0.dp
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.height(40.dp),
                text = {
                    Text(
                        category.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
private fun NotificationItem(
    notification: HubNotification,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
    onSnooze: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onTap),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Color(android.graphics.Color.parseColor(notification.category.colorHex)),
                        RoundedCornerShape(2.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    notification.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    notification.text,
                    fontSize = 12.sp,
                    color = Color(0xFFBBBBBB),
                    maxLines = 2
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.width(60.dp)
            ) {
                IconButton(
                    onClick = { onSnooze(300000) }, // 5 minutes
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Schedule,
                        contentDescription = "Snooze",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
