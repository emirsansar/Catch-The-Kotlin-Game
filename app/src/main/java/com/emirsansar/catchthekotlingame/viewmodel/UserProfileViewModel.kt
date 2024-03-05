package com.emirsansar.catchthekotlingame.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.emirsansar.catchthekotlingame.model.UserProfile
import com.emirsansar.catchthekotlingame.room.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class UserProfileViewModel(application: Application): BaseViewModel(application) {

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage


    fun getUserProfileInfoFromRoomDatabase(userEmail: String, callback: (UserProfile?) -> Unit?){
        launch {
            val userProfile = AppDatabase(getApplication()).userProfileDAO().getUserProfileInfo(userEmail)
            callback(userProfile)
        }
    }

    fun saveUserProfileToRoomDB(userProfile: UserProfile){
        launch {
            AppDatabase(getApplication()).userProfileDAO().insertUserProfileInfo(userProfile)
        }
    }

    fun getUserProfilePictureUriFromRoomDB(userEmail: String, callback: (Uri) -> Unit) {
        launch {
            val userProfile = AppDatabase(getApplication()).userProfileDAO().getUserProfileInfo(userEmail)
            callback(userProfile!!.userProfilePictureUri.toUri())
        }
    }

    fun updateUserProfilePictureUriInRoomDB(userEmail: String, uri: String){
        launch {
            AppDatabase(getApplication()).userProfileDAO().updateProfilePictureUri(userEmail, uri)
        }
    }

    fun uploadProfilePictureToFirebaseStorage(userEmail: String, bitmap: Bitmap, callback: (Uri) -> Unit){
        launch {
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
                            callback(uri)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(getApplication(), "Error updating profile image: ${exception.message}", Toast.LENGTH_SHORT).show()
                            callback(uri)
                        }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(getApplication(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun downloadProfilePictureFromFirebaseStorage(userEmail: String, callback: (Uri?) -> Unit) {
        launch {
            val storageReference = storage.reference
            val imageRef = storageReference.child("profile_images/$userEmail.jpg")

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri)
            }.addOnFailureListener {
                println("${it.localizedMessage}")
            }
        }
    }

    fun insertUserProfileToFirestore(email: String, name: String, surname: String, callback: (Boolean) -> Unit){
        launch {
            val user = hashMapOf("name" to name, "surname" to surname, "email" to email, "profileImageUrl" to null)

            firestore.collection("user_info").document(email)
                .set(user).addOnSuccessListener {
                    Toast.makeText(getApplication(), "Başarıyla kaydoldunuz!", Toast.LENGTH_LONG).show()
                    callback(true)
                }.addOnFailureListener { e ->
                    Toast.makeText(getApplication(),"H: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    callback(false)
                }
        }
    }

    fun getUserFullNameFromFirestore(userEmail: String, callback: (String) -> Unit){
        launch {
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
    }

    fun getUserFullNameFromRoomDB(userEmail: String, callback: (String) -> Unit){
        launch {
            val fullName = AppDatabase(getApplication()).userProfileDAO().getUserProfileInfo(userEmail)!!.userFullName
            callback(fullName)
        }
    }

    fun updateUserFullNameOnFirestore(userEmail: String, name: String, surname: String, callback: (Boolean) -> Unit){
        val userRef = firestore.collection("user_info").document(userEmail)

        userRef.get().addOnSuccessListener {
            userRef.update(
                mapOf("name" to name, "surname" to surname)
            ).addOnSuccessListener {
                Toast.makeText(getApplication(),"Your name is updated successfully.", Toast.LENGTH_SHORT).show()
                callback(true)
            }.addOnFailureListener {
                Toast.makeText(getApplication(),"An error occurred while updating your name.", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        }.addOnFailureListener { e->
            Log.e("HATA", "Hata oluştu: ${e.localizedMessage}", e)
        }
    }

    fun updateFullNameOnRoomDB(userEmail: String, fullName: String){
        launch {
            AppDatabase(getApplication()).userProfileDAO().updateProfileFullName(userEmail, fullName)
        }
    }
}