package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserSettingsEntity::class, PlayerCustomizationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "varzea_fc_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.gameDao())
                    }
                }
            }

            suspend fun populateInitialData(gameDao: GameDao) {
                gameDao.upsertUserSettings(
                    UserSettingsEntity(
                        id = 1,
                        gameDuration = "1:30",
                        graphicsQuality = "ALTO",
                        torcidaEnabled = true,
                        coins = 300
                    )
                )
                gameDao.upsertPlayerCustomization(
                    PlayerCustomizationEntity(
                        id = 1,
                        skinColorHex = "#E8B282",
                        hairStyleId = 5,
                        hairColorHex = "#212121",
                        shirtStyleId = 0,
                        shirtColorHex = "#68BBE3",
                        shortsColorHex = "#FFFFFF",
                        socksColorHex = "#68BBE3",
                        glovesColorHex = "none",
                        specialJersey = "NONE",
                        speed = 65,
                        kick = 65,
                        rotationAngle = 0
                    )
                )
            }
        }
    }
}
