package com.nofy.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nofy.core.feature.notification.permisos.PermissionManager
import com.nofy.core.feature.notification.service.NotificationSimulator
import com.nofy.domain.usecase.GetHomeDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val permissionManager: PermissionManager,
    private val notificationSimulator: NotificationSimulator,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        checkNotificationListenerPermission()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val data = getHomeDataUseCase()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    title = data.title,
                    description = data.description
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun checkNotificationListenerPermission() {
        val isEnabled = permissionManager.isNotificationListenerEnabled(context)
        _uiState.value = _uiState.value.copy(isServiceActive = isEnabled)
    }

    fun onToggleService() {
        if (!_uiState.value.isServiceActive) {
            openNotificationListenerSettings()
        }
    }

    fun onTestNotificationClick() {
        _uiState.value = _uiState.value.copy(showTestNotificationDialog = true)
    }

    fun onConfirmTestNotification() {
        _uiState.value = _uiState.value.copy(showTestNotificationDialog = false)
        notificationSimulator.sendWalletSimulation()
    }

    fun onDismissTestNotification() {
        _uiState.value = _uiState.value.copy(showTestNotificationDialog = false)
    }

    private fun openNotificationListenerSettings() {
        permissionManager.openNotificationListenerSettings(context)
    }
}
