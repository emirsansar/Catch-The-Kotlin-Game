package com.emirsansar.catchthekotlingame.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_records")
data class UserRecord(
    @PrimaryKey()
    val userEmail: String,
    @ColumnInfo("game_duration")
    val gameDuration: String,
    @ColumnInfo("record")
    val record: String
) {
}