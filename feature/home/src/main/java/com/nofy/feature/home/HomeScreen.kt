package com.nofy.feature.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nofy.core.ui.theme.Surface
import com.nofy.core.ui.theme.TextPrimary
import com.nofy.core.ui.theme.TextSecondary

private val ActiveGreen = Color(0xFF4CAF50)
private val InactiveRed = Color(0xFFE53935)

@Composable
fun HomeScreen(
    onNavigateToRegistros: () -> Unit = {},
    onNavigateToAddGasto: () -> Unit = {},
    onNavigateToConfiguracion: () -> Unit = {},
    onNavigateToPlantillas: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nofi),
                    contentDescription = "Nofy",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nofy",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNavigateToConfiguracion) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val toggleColor = if (uiState.isServiceActive) ActiveGreen else InactiveRed
            val toggleText = if (uiState.isServiceActive) "ACTIVADO" else "DESACTIVADO"

            Button(
                onClick = { viewModel.onToggleService() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = toggleColor
                )
            ) {
                Text(
                    text = toggleText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HomeActionButton(icon = Icons.AutoMirrored.Filled.List, label = "VER REGISTROS", onClick = onNavigateToRegistros)
            Spacer(modifier = Modifier.height(12.dp))
            HomeActionButton(icon = Icons.Default.Remove, label = "AÑADIR GASTO", onClick = onNavigateToAddGasto)
            Spacer(modifier = Modifier.height(12.dp))
            HomeActionButton(icon = Icons.Default.Add, label = "AÑADIR INGRESO", onClick = { })
            Spacer(modifier = Modifier.height(12.dp))
            HomeActionButton(icon = Icons.Default.Place, label = "LUGARES DE PAGO", onClick = onNavigateToConfiguracion)
            Spacer(modifier = Modifier.height(12.dp))
            HomeActionButton(icon = Icons.AutoMirrored.Filled.TextSnippet, label = "PLANTILLAS", onClick = onNavigateToPlantillas)
            Spacer(modifier = Modifier.height(12.dp))
            HomeActionButton(icon = Icons.Default.Notifications, label = "NOTIFICACIÓN PRUEBA", onClick = { viewModel.onTestNotificationClick() })
        }
    }

    if (uiState.showTestNotificationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissTestNotification() },
            title = {
                Text(
                    text = "Notificación de prueba",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Se enviará una notificación simulada de Google Wallet al móvil.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmTestNotification() }) {
                    Text("Sí, enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissTestNotification() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun HomeActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}
