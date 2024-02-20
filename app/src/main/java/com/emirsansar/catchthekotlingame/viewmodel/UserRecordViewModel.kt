package com.emirsansar.catchthekotlingame.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.emirsansar.catchthekotlingame.model.UserRecord
import com.emirsansar.catchthekotlingame.room.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class UserRecordViewModel(application: Application): BaseViewModel(application) {

    var userRecord = MutableLiveData<UserRecord>()

    private val firestore = Firebase.firestore

    fun fetchDataFromRoomDB(userEmail: String, duration: String, callback: (UserRecord?) -> Unit) {
        launch {
            val record = AppDatabase(getApplication()).userRecordDAO().getUserRecord(userEmail, duration).also {
            }
            callback(record)
        }
    }

    fun insertDataToRoomDB(userEmail: String, duration: String, newRecord: String) {
        launch {
            AppDatabase(getApplication()).userRecordDAO().insertUserRecord(UserRecord(userEmail, duration, newRecord))
        }
    }

    fun updateDataToRoomDB(userEmail: String, duration: String, newRecord: String) {
        launch {
            AppDatabase(getApplication()).userRecordDAO().updateUserRecord(userEmail, duration, newRecord)
        }
    }

}