package com.emirsansar.catchthekotlingame.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirsansar.catchthekotlingame.model.UserRecord

@Dao
interface UserRecordDAO {

    @Query("SELECT * FROM user_records WHERE userEmail = :userEmail AND game_duration = :duration")
    suspend fun getUserRecord(userEmail: String, duration: String): UserRecord?

    @Query("UPDATE user_records SET record = :newRecord WHERE userEmail = :userEmail AND game_duration = :duration")
    suspend fun updateUserRecord(userEmail: String, duration: String, newRecord: String)

    @Insert
    suspend fun insertUserRecord(userRecord: UserRecord)
}