package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.PlayerCustomizationEntity
import com.example.data.db.UserSettingsEntity
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenState {
    MAIN_MENU,
    SETTINGS,
    PLAYER_EDIT,
    HAIR_EDIT,
    MATCH_FIELD
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    val userSettings: StateFlow<UserSettingsEntity>
    val playerCustomization: StateFlow<PlayerCustomizationEntity>

    private val _currentScreen = MutableStateFlow(ScreenState.MAIN_MENU)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<ScreenState>()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = GameRepository(database.gameDao())

        userSettings = repository.userSettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettingsEntity()
        )

        playerCustomization = repository.playerCustomization.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerCustomizationEntity()
        )

        // Seed initial default if not yet inserted
        viewModelScope.launch {
            repository.updateSettings(userSettings.value)
            repository.updatePlayerCustomization(playerCustomization.value)
        }
    }

    fun navigateTo(screen: ScreenState) {
        if (_currentScreen.value != screen) {
            _screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (_screenHistory.isNotEmpty()) {
            _currentScreen.value = _screenHistory.removeAt(_screenHistory.size - 1)
            true
        } else if (_currentScreen.value != ScreenState.MAIN_MENU) {
            _currentScreen.value = ScreenState.MAIN_MENU
            true
        } else {
            false
        }
    }

    fun showNotification(msg: String) {
        _notificationMessage.value = msg
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }

    // --- Settings Updates ---
    fun setGameDuration(duration: String) {
        viewModelScope.launch {
            val updated = userSettings.value.copy(gameDuration = duration)
            repository.updateSettings(updated)
            showNotification("Tempo de jogo: $duration salvo!")
        }
    }

    fun setGraphicsQuality(quality: String) {
        viewModelScope.launch {
            val updated = userSettings.value.copy(graphicsQuality = quality)
            repository.updateSettings(updated)
            showNotification("Gráficos: $quality salvo!")
        }
    }

    fun setTorcidaEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val updated = userSettings.value.copy(torcidaEnabled = enabled)
            repository.updateSettings(updated)
            showNotification("Torcida: ${if (enabled) "SIM" else "NÃO"} salvo!")
        }
    }

    fun saveAllSettings() {
        viewModelScope.launch {
            repository.updateSettings(userSettings.value)
            showNotification("Configurações salvas com sucesso!")
        }
    }

    // --- Player Customization Updates ---
    fun setSkinColor(hex: String) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(skinColorHex = hex)
            repository.updatePlayerCustomization(updated)
        }
    }

    fun setHairStyle(styleId: Int) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(hairStyleId = styleId)
            repository.updatePlayerCustomization(updated)
        }
    }

    fun setHairColor(hex: String) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(hairColorHex = hex)
            repository.updatePlayerCustomization(updated)
        }
    }

    fun setShirtColor(hex: String) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(
                shirtColorHex = hex,
                specialJersey = "NONE"
            )
            repository.updatePlayerCustomization(updated)
        }
    }

    fun setShortsColor(hex: String) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(shortsColorHex = hex)
            repository.updatePlayerCustomization(updated)
        }
    }

    fun setSocksColor(hex: String) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(socksColorHex = hex)
            repository.updatePlayerCustomization(updated)
        }
    }

    fun setGlovesColor(hex: String) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(glovesColorHex = hex)
            repository.updatePlayerCustomization(updated)
        }
    }

    fun setSpecialJersey(jerseyKey: String) {
        viewModelScope.launch {
            val updated = playerCustomization.value.copy(
                specialJersey = jerseyKey,
                shirtColorHex = when (jerseyKey) {
                    "VARZEA_3D" -> "#1E1E1E"
                    "JESUS_TE_AMA" -> "#B71C1C"
                    "FIRE" -> "#E65100"
                    "PRIME" -> "#FBC02D"
                    "SALMO_91" -> "#263238"
                    "DEUS_NO_CORACAO" -> "#0D47A1"
                    else -> playerCustomization.value.shirtColorHex
                }
            )
            repository.updatePlayerCustomization(updated)
            showNotification("Camisa Especial $jerseyKey equipada!")
        }
    }

    fun rotatePlayer() {
        viewModelScope.launch {
            val nextAngle = (playerCustomization.value.rotationAngle + 1) % 3
            val updated = playerCustomization.value.copy(rotationAngle = nextAngle)
            repository.updatePlayerCustomization(updated)
        }
    }

    fun upgradeSpeed() {
        val currentCoins = userSettings.value.coins
        val cost = 50
        if (currentCoins >= cost) {
            viewModelScope.launch {
                val updatedCoins = currentCoins - cost
                repository.updateSettings(userSettings.value.copy(coins = updatedCoins))
                val updatedCustom = playerCustomization.value.copy(
                    speed = (playerCustomization.value.speed + 1).coerceAtMost(99)
                )
                repository.updatePlayerCustomization(updatedCustom)
                showNotification("Velocidade aumentada para ${updatedCustom.speed}!")
            }
        } else {
            showNotification("Moedas insuficientes! Precisa de $cost moedas.")
        }
    }

    fun upgradeKick() {
        val currentCoins = userSettings.value.coins
        val cost = 50
        if (currentCoins >= cost) {
            viewModelScope.launch {
                val updatedCoins = currentCoins - cost
                repository.updateSettings(userSettings.value.copy(coins = updatedCoins))
                val updatedCustom = playerCustomization.value.copy(
                    kick = (playerCustomization.value.kick + 1).coerceAtMost(99)
                )
                repository.updatePlayerCustomization(updatedCustom)
                showNotification("Chute aumentado para ${updatedCustom.kick}!")
            }
        } else {
            showNotification("Moedas insuficientes! Precisa de $cost moedas.")
        }
    }

    fun rewardMatchCoins(coinsGained: Int) {
        viewModelScope.launch {
            val updatedCoins = userSettings.value.coins + coinsGained
            repository.updateSettings(userSettings.value.copy(coins = updatedCoins))
        }
    }
}
