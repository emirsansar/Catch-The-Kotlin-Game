package com.emirsansar.catchthekotlingame.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
class UserProfile(
    @PrimaryKey()
    val userEmail: String,
    @ColumnInfo("user_full_name")
    var userFullName: String,
    @ColumnInfo("user_profile_picture_uri")
    var userProfilePictureUri: String
) {
}