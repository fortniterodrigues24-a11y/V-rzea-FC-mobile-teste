package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_customization")
data class PlayerCustomizationEntity(
    @PrimaryKey
    val id: Int = 1,
    val skinColorHex: String = "#E8B282",
    val hairStyleId: Int = 5, // Default: Headband with beard as seen in gameplay!
    val hairColorHex: String = "#212121",
    val shirtStyleId: Int = 0,
    val shirtColorHex: String = "#68BBE3", // Cyan / light blue
    val shortsColorHex: String = "#FFFFFF", // White
    val socksColorHex: String = "#68BBE3", // Cyan
    val glovesColorHex: String = "none",
    val specialJersey: String = "NONE",
    val speed: Int = 65,
    val kick: Int = 65,
    val rotationAngle: Int = 0, // 0 = Front, 1 = 45 deg, 2 = Profile/Side (Image 4)
    val updatedAt: Long = System.currentTimeMillis()
)
