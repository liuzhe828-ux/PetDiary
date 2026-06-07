package com.petdiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.petdiary.data.dao.DiaryDao
import com.petdiary.data.dao.PetStateDao
import com.petdiary.data.dao.TaskDao
import com.petdiary.data.entity.DiaryEntry
import com.petdiary.data.entity.PetState
import com.petdiary.data.entity.TaskItem

@Database(
    entities = [PetState::class, DiaryEntry::class, TaskItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petStateDao(): PetStateDao
    abstract fun diaryDao(): DiaryDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pet_diary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
