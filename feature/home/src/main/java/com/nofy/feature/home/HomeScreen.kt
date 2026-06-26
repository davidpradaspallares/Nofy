package com.nofy.feature.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nofy.core.ui.components.NofyCard
import com.nofy.core.ui.components.NotificationItem
import com.nofy.core.ui.theme.TextPrimary
import com.nofy.core.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val postNotificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.simulateNotification()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkNotificationListenerPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val onSimulateClick: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            postNotificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.simulateNotification()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                NofyCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = uiState.title)
                        Text(
                            text = uiState.description,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (!uiState.isNotificationListenerEnabled) {
                item {
                    NofyCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Acceso a notificaciones desactivado",
                                color = TextPrimary
                            )
                            Text(
                                text = "Para capturar notificaciones, habilita Nofy en los ajustes del sistema.",
                                color = TextSecondary
                            )
                            Button(
                                onClick = { viewModel.openNotificationListenerSettings() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Abrir ajustes")
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onSimulateClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simular Notificación")
                }
            }

            if (uiState.notifications.isEmpty()) {
                item {
                    Text(
                        text = "No hay notificaciones capturadas",
                        color = TextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(uiState.notifications, key = { "${it.packageName}_${it.postTime}" }) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        }
    }
}
