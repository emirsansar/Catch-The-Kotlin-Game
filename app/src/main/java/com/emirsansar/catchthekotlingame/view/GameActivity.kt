package com.emirsansar.catchthekotlingame.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.emirsansar.catchthekotlingame.GameManager
import com.emirsansar.catchthekotlingame.databinding.ActivityGameBinding
import com.emirsansar.catchthekotlingame.viewmodel.UserRecordViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val duration = intent.getStringExtra("DURATION")

        gameManager = GameManager(this, duration!!, binding)

        setListeners()
    }

    private fun setListeners(){
        binding.btnStartGame.setOnClickListener {
            gameManager.startGame()
        }

        binding.btnBack.setOnClickListener {
            backToMainScreen()
        }
    }

    private fun backToMainScreen(){
        val alert = AlertDialog.Builder(this)
        alert.setTitle("BACK")
        alert.setMessage("Do you want to back to main menu?")

        alert.setPositiveButton("Yes") { _, _ ->
            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        alert.setNegativeButton("No", null)

        alert.show()
    }
}