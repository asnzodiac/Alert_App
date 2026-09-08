package com.example.smsalarmtrigger.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smsalarmtrigger.R
import com.example.smsalarmtrigger.data.AlarmSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    settings: AlarmSettings,
    hasSmsPermission: Boolean,
    hasNotificationPermission: Boolean,
    isBatteryOptimizationIgnored: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    onKeywordChanged: (String) -> Unit,
    onSenderFilterChanged: (String) -> Unit,
    onTestAlarm: () -> Unit,
    onStopAlarm: () -> Unit,
    onRequestSmsPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestDisableBatteryOptimization: () -> Unit
) {
    var keywordInput by remember(settings.triggerKeyword) { mutableStateOf(settings.triggerKeyword) }
    var senderInput by remember(settings.senderFilter) { mutableStateOf(settings.senderFilter) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alarmScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (settings.isAlarmActive) Color(0xFFEF4444)
                                    else if (settings.isEnabled) Color(0xFF22C55E)
                                    else Color(0xFF6B7280),
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // EMERGENCY RINGING OVERLAY BANNER
            AnimatedVisibility(visible = settings.isAlarmActive) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(pulseScale),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF991B1B))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "ALARM ACTIVE & RINGING",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Max volume override active. Tap below to dismiss.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )
                        Button(
                            onClick = onStopAlarm,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF991B1B)
                            )
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SILENCE ALARM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // MASTER CONTROL CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (settings.isEnabled) Icons.Default.NotificationsActive else Icons.Default.Alarm,
                            contentDescription = null,
                            tint = if (settings.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (settings.isEnabled) "Service Online" else "Service Paused",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (settings.isEnabled) "Listening for incoming trigger text" else "SMS listener is currently inactive",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = settings.isEnabled,
                        onCheckedChange = onToggleEnabled
                    )
                }
            }

            // CONFIGURATION INPUTS CARD
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Trigger Rules",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    OutlinedTextField(
                        value = keywordInput,
                        onValueChange = {
                            keywordInput = it
                            onKeywordChanged(it)
                        },
                        label = { Text("Keyword Phrase") },
                        supportingText = { Text("Case-insensitive match inside SMS text") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = senderInput,
                        onValueChange = {
                            senderInput = it
                            onSenderFilterChanged(it)
                        },
                        label = { Text("Sender Filter (Optional)") },
                        supportingText = { Text("Leave blank to accept triggers from anyone") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // TESTING MODULE CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hardware Audio Test",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Test your alarm audio stream output and vibration patterns instantly.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )
                    Button(
                        onClick = onTestAlarm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger Test Alarm", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // SYSTEM PERMISSIONS PANEL
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "System Permissions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    PermissionStatusRow(
                        title = "SMS Interception",
                        description = "Required to scan incoming messages.",
                        isGranted = hasSmsPermission,
                        onAction = onRequestSmsPermission
                    )
                    PermissionStatusRow(
                        title = "Notifications & Alerts",
                        description = "Required to host the persistent alarm service.",
                        isGranted = hasNotificationPermission,
                        onAction = onRequestNotificationPermission
                    )
                    PermissionStatusRow(
                        title = "Battery Exemption",
                        description = "Prevents deep-sleep from blocking alarms.",
                        isGranted = isBatteryOptimizationIgnored,
                        onAction = onRequestDisableBatteryOptimization
                    )
                }
            }

            // AUDIT TRAIL LOG
            if (settings.lastTriggeredTime > 0L) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Last Trigger Activity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val formattedDate = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()).format(Date(settings.lastTriggeredTime))
                        Text(text = "Time: $formattedDate", fontSize = 12.sp)
                        if (settings.lastTriggeredSender.isNotBlank()) {
                            Text(text = "Sender: ${settings.lastTriggeredSender}", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionStatusRow(
    title: String,
    description: String,
    isGranted: Boolean,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isGranted) Color(0xFF4ADE80).copy(alpha = 0.4f) else Color(0xFFF87171).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .background(if (isGranted) Color(0xFFF0FDF4).copy(alpha = 0.6f) else Color(0xFFFEF2F2).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                if (isGranted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                }
            }
            Text(text = description, fontSize = 11.sp, color = Color.Gray)
        }
        if (!isGranted) {
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Grant", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
