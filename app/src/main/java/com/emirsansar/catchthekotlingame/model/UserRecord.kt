package com.emirsansar.catchthekotlingame.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_records")
data class UserRecord(
    @PrimaryKey()
    val userEmail: String,
    @ColumnInfo("record_10Second")
    var record_10Second: String,
    @ColumnInfo("record_30Second")
    var record_30Second: String,
    @ColumnInfo("record_60Second")
    var record_60Second: String
) {
}