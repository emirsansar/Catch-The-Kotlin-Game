package com.emirsansar.catchthekotlingame.viewmodel

import android.app.Application
import com.emirsansar.catchthekotlingame.model.Rank
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class RankingViewModel(application: Application): BaseViewModel(application) {

    private val firestore = Firebase.firestore

    fun getRankingList(second: String, callback: (List<Rank>) -> Unit) {
        val rankingList = arrayListOf<Rank>()

        firestore.collection("rank_$second"+"seconds")
            .orderBy("score", Query.Direction.DESCENDING).limit(15).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val score = (document.get("score") as? Number)!!.toInt()
                    val userEmail = document.id

                    val rank = Rank(score, userEmail)
                    rankingList.add(rank)
                }
                callback(rankingList)
            }.addOnFailureListener {
                callback(emptyList())
            }
    }
}