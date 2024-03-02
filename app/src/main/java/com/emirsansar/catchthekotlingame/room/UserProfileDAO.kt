package com.emirsansar.catchthekotlingame.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirsansar.catchthekotlingame.model.UserProfile

@Dao
interface UserProfileDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfileInfo(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE userEmail = :userEmail ")
    suspend fun getUserProfileInfo(userEmail: String): UserProfile?

    @Query("UPDATE user_profile SET user_profile_picture_uri = :uri WHERE userEmail = :userEmail")
    suspend fun setProfilePictureUri(userEmail: String, uri: String)
}