package com.example.spaceintruders.data

import android.content.Context
import android.os.strictmode.InstanceCountViolation
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GameRecord::class], version = 2, exportSchema = true)
@TypeConverters(Converter::class)
abstract class GameRecordDatabase : RoomDatabase() {

    abstract fun gameRecordDao(): GameRecordDao

    companion object {
        @Volatile
        private var INSTANCE: GameRecordDatabase? = null;

        fun getDatabase(context: Context): GameRecordDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameRecordDatabase::class.java,
                    "game_record_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}