package com.emirsansar.catchthekotlingame.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emirsansar.catchthekotlingame.model.UserProfile
import com.emirsansar.catchthekotlingame.model.UserRecord

@Database(entities = [UserRecord::class, UserProfile::class], version = 4)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userRecordDAO(): UserRecordDAO
    abstract fun userProfileDAO(): UserProfileDAO

    companion object{

        @Volatile private var instance: AppDatabase? = null

        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            instance ?: makeDatabase(context).also {
                instance = it
            }
        }

        private fun makeDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, "app_database"
        ).fallbackToDestructiveMigration().build()
    }
}