package com.emirsansar.catchthekotlingame.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirsansar.catchthekotlingame.model.UserRecord

@Dao
interface UserRecordDAO {

    @Query("SELECT * FROM user_records WHERE userEmail = :userEmail ")
    suspend fun getUserRecord(userEmail: String): UserRecord?

    @Query("UPDATE user_records SET record_10Second = :newRecord WHERE userEmail = :userEmail")
    suspend fun updateUserRecordFor10Sec(userEmail: String, newRecord: String)

    @Query("UPDATE user_records SET record_30Second = :newRecord WHERE userEmail = :userEmail")
    suspend fun updateUserRecordFor30Sec(userEmail: String, newRecord: String)

    @Query("UPDATE user_records SET record_60Second = :newRecord WHERE userEmail = :userEmail")
    suspend fun updateUserRecordFor60Sec(userEmail: String, newRecord: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRecord(userRecord: UserRecord)
}