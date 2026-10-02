package com.example.data.repository

import com.example.data.db.GameDao
import com.example.data.db.PlayerCustomizationEntity
import com.example.data.db.UserSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val gameDao: GameDao) {

    val userSettings: Flow<UserSettingsEntity> = gameDao.getUserSettings().map { settings ->
        settings ?: UserSettingsEntity()
    }

    val playerCustomization: Flow<PlayerCustomizationEntity> = gameDao.getPlayerCustomization().map { custom ->
        custom ?: PlayerCustomizationEntity()
    }

    suspend fun updateSettings(settings: UserSettingsEntity) {
        gameDao.upsertUserSettings(settings.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun updatePlayerCustomization(customization: PlayerCustomizationEntity) {
        gameDao.upsertPlayerCustomization(customization.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateCoins(delta: Int) {
        // Safe coin adjustment
    }
}
