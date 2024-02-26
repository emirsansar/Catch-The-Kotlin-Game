package com.emirsansar.catchthekotlingame.view.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.emirsansar.catchthekotlingame.databinding.FragmentHomeBinding
import com.emirsansar.catchthekotlingame.view.GameActivity
import com.emirsansar.catchthekotlingame.view.login.LoginActivity
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

        setSpinnerListener()
        setBtnPlayGameListener()
        setBtnQuitListener()
    }


    private fun setBtnPlayGameListener(){
        binding.btnStart.setOnClickListener {
            val intent = Intent(requireContext(), GameActivity::class.java)
            intent.putExtra("DURATION", duration)
            intent.putExtra("USER_EMAIL", auth.currentUser!!.email)
            startActivity(intent)
            //finish()
        }
    }

    private fun setSpinnerListener(){
        binding.spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                duration = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun setBtnQuitListener(){
        binding.btnQuit.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Quit Game")
                .setMessage("Are you sure you want to exit the game?")
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

}