package com.emirsansar.catchthekotlingame.view.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.emirsansar.catchthekotlingame.databinding.FragmentHomeBinding
import com.emirsansar.catchthekotlingame.view.game.GameActivity
import com.emirsansar.catchthekotlingame.viewmodel.UserRecordViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var duration: String

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser : FirebaseUser? = null

    private lateinit var viewModel : UserRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = Firebase.firestore
        auth = Firebase.auth
        currentUser = auth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this)[UserRecordViewModel::class.java]

        setSpinnerListener()
        setBtnPlayGameListener()
    }


    private fun setBtnPlayGameListener(){
        binding.btnStart.setOnClickListener {
            val userEmail = currentUser!!.email.toString()

            val intent = Intent(requireContext(), GameActivity::class.java)
            intent.putExtra("DURATION", duration)
            intent.putExtra("USER_EMAIL", userEmail)

            viewModel.getUserRecordFromRoomDB(userEmail) { userRecord ->
                when (duration){
                    "10" -> {
                        intent.putExtra("HIGHEST_SCORE", userRecord!!.record_10Second.toInt())}
                    "30" -> {
                        intent.putExtra("HIGHEST_SCORE", userRecord!!.record_30Second.toInt())}
                    else -> {
                        intent.putExtra("HIGHEST_SCORE", userRecord!!.record_60Second.toInt())}
                }
                startActivity(intent)
            }
        }
    }

    private fun setSpinnerListener(){
        binding.spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                duration = parent?.getItemAtPosition(position).toString().substringBefore(" ")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

}