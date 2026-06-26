package com.nofy.feature.home

import com.nofy.core.service.notification.model.NotificationData

data class HomeUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val notifications: List<NotificationData> = emptyList(),
    val isNotificationListenerEnabled: Boolean = true,
    val selectedNotificationJson: String? = null,
    val selectedNotificationDate: String? = null,
)
