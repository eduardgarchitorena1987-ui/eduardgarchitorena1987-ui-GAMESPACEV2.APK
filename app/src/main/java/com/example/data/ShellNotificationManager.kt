package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ShellNotification(
    val id: String = UUID.randomUUID().toString(),
    val command: String,
    val output: String,
    val subsystem: String,
    val executionTimeMs: Long = 2,
    val returnCode: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

object ShellNotificationManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val _currentNotification = MutableStateFlow<ShellNotification?>(null)
    val currentNotification: StateFlow<ShellNotification?> = _currentNotification

    private val _history = MutableStateFlow<List<ShellNotification>>(emptyList())
    val history: StateFlow<List<ShellNotification>> = _history

    fun notifyTweak(command: String, subsystem: String, output: String = "SUCCESS [RC 0]") {
        val notif = ShellNotification(
            command = command,
            output = output,
            subsystem = subsystem,
            executionTimeMs = (1L..4L).random()
        )
        _currentNotification.value = notif
        _history.value = listOf(notif) + _history.value.take(20)

        scope.launch {
            delay(3200)
            if (_currentNotification.value?.id == notif.id) {
                _currentNotification.value = null
            }
        }
    }
}
