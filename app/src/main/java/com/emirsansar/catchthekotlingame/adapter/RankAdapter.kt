package com.emirsansar.catchthekotlingame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.databinding.RowRankBinding
import com.emirsansar.catchthekotlingame.model.Rank
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class RankAdapter(private val rankList: ArrayList<Rank>, private var storage: FirebaseStorage, private var firestore: FirebaseFirestore): RecyclerView.Adapter<RankAdapter.RowHolder>() {

    class RowHolder(val binding: RowRankBinding) : RecyclerView.ViewHolder(binding.root) {
    }


    fun updateData(newList: List<Rank>) {
        rankList.clear()
        rankList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val binding = RowRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowHolder(binding)
    }

    override fun getItemCount(): Int {
        return rankList.count()
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val currentRank = rankList[position]

        var userFullName: String?
        val userScore = currentRank.userScore
        val userEmail = currentRank.userEmail

        firestore.collection("user_info").document(userEmail).get().addOnSuccessListener { docSnapshot ->
            val name = docSnapshot.getString("name")
            val surname = docSnapshot.getString("surname")

            userFullName = "$name $surname"

            holder.binding.textRank.text = (position+1).toString()
            holder.binding.textFullname.text = userFullName
            holder.binding.textScore.text = userScore.toString()

            if (position < 3){
                val color = when (position){
                    0 -> R.color.rank_first
                    1 -> R.color.rank_second
                    else -> R.color.rank_third
                }
                holder.binding.root.setBackgroundColor(holder.itemView.context.resources.getColor(color))
            }

            val storageRef = storage.reference.child("profile_images/$userEmail.jpg")
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(holder.binding.profilePicture)
            }.addOnFailureListener {
                holder.binding.profilePicture.setImageResource(R.drawable.default_profile_picture)
            }
        }


    }

}