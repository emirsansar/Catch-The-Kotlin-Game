package com.emirsansar.catchthekotlingame.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.emirsansar.catchthekotlingame.manager.GameManager
import com.emirsansar.catchthekotlingame.databinding.ActivityGameBinding
import com.emirsansar.catchthekotlingame.view.main.MainActivity

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val duration = intent.getStringExtra("DURATION")
        val userEmail = intent.getStringExtra("USER_EMAIL")
        val highestScore = intent.getStringExtra("SCORE")
        binding.highestScore.text = "Highest Score: $highestScore"

        gameManager = GameManager(this, userEmail!!, duration!!, highestScore!!, binding)

        setListeners()
    }

    private fun setListeners(){
        binding.btnStartGame.setOnClickListener {
            binding.textCountDownForReady.visibility = View.VISIBLE
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