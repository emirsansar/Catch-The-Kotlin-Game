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

    var userRecord = MutableLiveData<UserRecord>()

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    var userRecords = mutableMapOf<String,Int>()

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

    fun getUserScoreFor10Second(userEmail: String, callback: (Int?) -> Unit){
        launch {
            firestore.collection("rank_10seconds").document(userEmail).get()
                .addOnSuccessListener {
                    val score = (it.get("score") as? Number)?.toInt() ?: 0

                    callback(score)
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    callback(0)
                }
        }
    }

    fun getUserScoreFor30Second(userEmail: String, callback: (Int?) -> Unit){
        launch {
            firestore.collection("rank_30seconds").document(userEmail).get()
                .addOnSuccessListener {
                    val score = (it.get("score") as? Number)?.toInt() ?: 0

                    callback(score)
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    callback(0)
                }
        }
    }

    fun getUserScoreFor60Second(userEmail: String, callback: (Int?) -> Unit){
        launch {
            firestore.collection("rank_60seconds").document(userEmail).get()
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

    fun uploadImageToFirebaseStorage(bitmap: Bitmap, userEmail: String){
        val storageReference = storage.reference
        val imageRef = storageReference.child("profile_images/$userEmail.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()

                val userRef = firestore.collection("user_info").document(userEmail)
                userRef.update("profileImageUrl", downloadUrl)
                    .addOnSuccessListener {
                        Toast.makeText(getApplication(), "Upload successful.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(getApplication(), "Error updating profile image: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(getApplication(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

     fun downloadImageFromFirebaseStorage(userEmail: String, callback: (Uri?) -> Unit) {
        val storageReference = storage.reference
        val imageRef = storageReference.child("profile_images/$userEmail.jpg")

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            callback(uri)
        }.addOnFailureListener {
            println("${it.localizedMessage}")
        }
    }

    fun getUserFullName(userEmail: String, callback: (String) -> Unit){
        firestore.collection("user_info").document(userEmail).get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("name")
                val surname = documentSnapshot.getString("surname")

                if (!name.isNullOrEmpty() && !surname.isNullOrEmpty()) {
                    val fullName = "$name $surname"
                    callback(fullName)
                }
            }.addOnFailureListener { e->
                Log.e("HATA", "Hata oluştu: ${e.localizedMessage}", e)
            }
    }

    fun changeUserFullName(userEmail: String, name: String, surname: String){
        val userRef = firestore.collection("user_info").document(userEmail)

        userRef.get().addOnSuccessListener {
            userRef.update(
                mapOf("name" to name, "surname" to surname)
            ).addOnSuccessListener {
                Toast.makeText(getApplication(),"Your name is updated successfully.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(getApplication(),"An error occurred while updating your name.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e->
            Log.e("HATA", "Hata oluştu: ${e.localizedMessage}", e)
        }
    }
}