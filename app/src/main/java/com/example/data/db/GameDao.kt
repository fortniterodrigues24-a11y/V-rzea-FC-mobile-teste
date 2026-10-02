package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserSettings(settings: UserSettingsEntity)

    @Query("SELECT * FROM player_customization WHERE id = 1 LIMIT 1")
    fun getPlayerCustomization(): Flow<PlayerCustomizationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayerCustomization(customization: PlayerCustomizationEntity)
}
