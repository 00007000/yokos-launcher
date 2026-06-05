package app.lawnchair.bb10hub

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Bb10SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val darkColorScheme = darkColorScheme(
                primary = Color(0xFF0084FF),
                secondary = Color(0xFF31A24C),
                background = Color(0xFF1A1A1A),
                surface = Color(0xFF2A2A2A),
                onBackground = Color.White,
                onSurface = Color.White
            )
            
            MaterialTheme(colorScheme = darkColorScheme) {
                SettingsScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    val repository = HubRepositoryManager.getInstance()
    val settings by repository.settings.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences(
        "bb10_hub_prefs",
        android.content.Context.MODE_PRIVATE
    )
    
    var peekEnabled by remember { mutableStateOf(settings.peekEnabled) }
    var ledEnabled by remember { mutableStateOf(settings.ledEnabled) }
    var groupByCategory by remember { mutableStateOf(settings.groupByCategory) }
    var diagnosticsEnabled by remember { mutableStateOf(settings.diagnosticsEnabled) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        TopAppBar(
            title = { Text("BB10 Hub Settings") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2A2A2A),
                titleContentColor = Color.White
            )
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingToggle(
                    title = "Peek Animation",
                    description = "Show notification peek when new message arrives",
                    checked = peekEnabled,
                    onChanged = { newValue ->
                        peekEnabled = newValue
                        repository.updateSettings(settings.copy(peekEnabled = newValue))
                        prefs.edit().putBoolean("peek_enabled", newValue).apply()
                    }
                )
            }
            
            item {
                SettingToggle(
                    title = "LED Light",
                    description = "Flash Xperia LED per notification category",
                    checked = ledEnabled,
                    onChanged = { newValue ->
                        ledEnabled = newValue
                        repository.updateSettings(settings.copy(ledEnabled = newValue))
                        prefs.edit().putBoolean("led_enabled", newValue).apply()
                    }
                )
            }
            
            item {
                SettingToggle(
                    title = "Group by Category",
                    description = "Sort notifications by type (email, SMS, social, etc.)",
                    checked = groupByCategory,
                    onChanged = { newValue ->
                        groupByCategory = newValue
                        repository.updateSettings(settings.copy(groupByCategory = newValue))
                        prefs.edit().putBoolean("group_by_category", newValue).apply()
                    }
                )
            }
            
            item {
                SettingToggle(
                    title = "Diagnostics Logging",
                    description = "Log app events for debugging (anonymized)",
                    checked = diagnosticsEnabled,
                    onChanged = { newValue ->
                        diagnosticsEnabled = newValue
                        repository.updateSettings(settings.copy(diagnosticsEnabled = newValue))
                        prefs.edit().putBoolean("diagnostics_enabled", newValue).apply()
                    }
                )
            }
            
            item {
                Button(
                    onClick = {
                        // Export anonymized log
                        // TODO: Implement log export
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export Diagnostics Log")
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                color = Color(0xFFBBBBBB),
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onChanged,
            modifier = Modifier.size(48.dp)
        )
    }
}
