package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val gameDuration: String = "1:30",
    val graphicsQuality: String = "ALTO",
    val torcidaEnabled: Boolean = true,
    val coins: Int = 300,
    val updatedAt: Long = System.currentTimeMillis()
)
