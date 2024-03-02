package com.emirsansar.catchthekotlingame.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.model.UserRecord
import com.emirsansar.catchthekotlingame.room.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class UserRecordViewModel(application: Application): BaseViewModel(application) {

    private val firestore = Firebase.firestore

    fun fetchDataFromRoomDB(userEmail: String, callback: (UserRecord?) -> Unit) {
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

    fun changeUserScoreFor10SecOnRoom(userEmail: String, score: Int){
        launch {
            AppDatabase(getApplication()).userRecordDAO().updateUserRecordFor10Sec(userEmail, score.toString())
        }
    }

    fun changeUserScoreFor30SecOnRoom(userEmail: String, score: Int){
        launch {
            AppDatabase(getApplication()).userRecordDAO().updateUserRecordFor30Sec(userEmail, score.toString())
        }
    }

    fun changeUserScoreFor60SecOnRoom(userEmail: String, score: Int){
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

    fun setUserScoreToFirestore(userEmail: String, duration: String, score: Int){
        launch {
            val userRef = firestore.collection("rank_$duration"+"seconds").document(userEmail)

            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    userRef.update("score", score)
                        .addOnSuccessListener { println("Başarılı.") }
                        .addOnFailureListener { e -> e.printStackTrace() }
                }
                else {
                    val userData = hashMapOf("score" to score)
                    userRef.set(userData)
                        .addOnSuccessListener { println("Başarılı.") }
                        .addOnFailureListener { e -> e.printStackTrace() }
                }
            }.addOnFailureListener { e -> e.printStackTrace() }
        }
    }

}