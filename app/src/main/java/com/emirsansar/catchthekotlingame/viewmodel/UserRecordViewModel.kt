package com.emirsansar.catchthekotlingame.viewmodel

import android.app.Application
import com.emirsansar.catchthekotlingame.model.UserRecord
import com.emirsansar.catchthekotlingame.room.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class UserRecordViewModel(application: Application): BaseViewModel(application) {

    private val firestore = Firebase.firestore

    fun getUserRecordFromRoomDB(userEmail: String, callback: (UserRecord?) -> Unit) {
        launch {
            val record = AppDatabase(getApplication()).userRecordDAO().getUserRecord(userEmail).also {
            }
            callback(record)
        }
    }

    fun insertDataToRoomDB(userRecord: UserRecord) {
        launch {
            AppDatabase(getApplication()).userRecordDAO().insertUserRecord(userRecord)
        }
    }

    fun updateUserScoreFor10SecOnRoom(userEmail: String, score: Int){
        launch {
            AppDatabase(getApplication()).userRecordDAO().updateUserRecordFor10Sec(userEmail, score.toString())
        }
    }

    fun updateUserScoreFor30SecOnRoom(userEmail: String, score: Int){
        launch {
            AppDatabase(getApplication()).userRecordDAO().updateUserRecordFor30Sec(userEmail, score.toString())
        }
    }

    fun updateUserScoreFor60SecOnRoom(userEmail: String, score: Int){
        launch {
            AppDatabase(getApplication()).userRecordDAO().updateUserRecordFor60Sec(userEmail, score.toString())
        }
    }


    fun getUserScoreFromFirestore(userEmail: String, duration: String, callback: (Int?) -> Unit){
        launch {
            firestore.collection("rank_$duration"+"seconds").document(userEmail).get()
                .addOnSuccessListener {
                    val score = (it.get("score") as? Number)?.toInt() ?: 0

                    callback(score)
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    callback(0)
                }
        }
    }

    fun updateUserScoreToFirestore(userEmail: String, duration: String, score: Int, callback: (Boolean?) -> Unit){
        launch {
            val userRef = firestore.collection("rank_$duration"+"seconds").document(userEmail)

            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    userRef.update("score", score)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                            callback(false)
                        }
                } else {
                    val userData = hashMapOf("score" to score)
                    userRef.set(userData)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                            callback(false)
                        }
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
                callback(false)
            }
        }
    }

}