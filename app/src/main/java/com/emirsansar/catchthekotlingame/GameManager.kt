package com.emirsansar.catchthekotlingame

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.emirsansar.catchthekotlingame.databinding.ActivityGameBinding
import com.emirsansar.catchthekotlingame.view.GameActivity
import com.emirsansar.catchthekotlingame.viewmodel.UserRecordViewModel
import java.util.Random

class GameManager(private val context: Context, private val userEmail: String, private val duration: String, private val binding: ActivityGameBinding) {

    private var score: Int = 0
    private var userHighestScore: Int = 0
    private var runnable = Runnable {}
    private var handler = Handler(Looper.getMainLooper())

    private var viewModel = ViewModelProvider(context as GameActivity)[UserRecordViewModel::class.java]

    private lateinit var kotlinView: ImageView

    init {
        viewModel.getUserScoreFromFirestore(userEmail, duration){ score ->
            userHighestScore = score!!
            binding.highestScore.text = "Highest Score: $score"
        }

        setKotlinView()
    }


    private fun setKotlinView(){
        kotlinView = binding.kotlinImageView

        setKotlinViewListener()
    }

    fun startGame() {
        binding.textPressToPlay.visibility = View.GONE
        setButtonEnabled(false)
        startCountDownForReady()
    }


    fun showKotlinView() {
        runnable = object : Runnable {
            override fun run() {
                val random = Random()

                val layoutParams = kotlinView.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.leftMargin = random.nextInt(binding.consLayoutForGridLayout.width - kotlinView.width)
                layoutParams.topMargin = random.nextInt(binding.consLayoutForGridLayout.height - kotlinView.height)
                kotlinView.layoutParams = layoutParams

                handler.postDelayed(runnable, 500)
            }
        }
        handler.post(runnable)

    }



    fun startCountDownInPlaying() {
        object : CountDownTimer((duration.toInt() * 1000 + 500).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val formattedTime = String.format("%02d", millisUntilFinished / 1000)
                binding.timeText.text = "Time: $formattedTime"
            }

            override fun onFinish() {
                binding.timeText.text = "The game has ended."
                binding.imgClock.visibility = View.INVISIBLE

                handler.removeCallbacks(runnable)

                checkHighestScore(score)

                setButtonEnabled(true)
                showGameOverDialog()
            }
        }.start()
    }

    private fun startCountDownForReady() {
        object : CountDownTimer((3 * 1000 + 500).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val formattedTime = String.format("%01d", millisUntilFinished / 1000)
                binding.textCountDownForReady.text = "$formattedTime"
            }

            override fun onFinish() {
                showKotlinView()
                startCountDownInPlaying()

                binding.textCountDownForReady.visibility = View.GONE
                kotlinView.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun increaseScore() {
        score++
        binding.scoreText.text = "Score: $score"
    }

    fun checkHighestScore(score: Int){
        if (score > userHighestScore){
            binding.highestScore.text = "Highest Score: $score"
            viewModel.setUserScoreToFirestore(userEmail, duration, score)

            Toast.makeText(context, "Congratulations. New record!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setKotlinViewListener(){
        binding.kotlinImageView.setOnClickListener {
            increaseScore()
        }
    }

//    private fun updateUserRecord(){
//        viewModel.fetchDataFromRoomDB(userEmail, duration){ userRecord ->
//            if (userRecord != null){
//                if (userRecord.record.toInt() < score){
//                    binding.highestScore.text = "Highest Score: ${score}"
//                    viewModel.updateDataToRoomDB(userEmail, duration, score.toString())
//                }
//            } else {
//                viewModel.insertDataToRoomDB(userEmail, duration, score.toString())
//                binding.highestScore.text = "Highest Score: $score"
//            }
//        }
//    }


    private fun restartGame() {
        score = 0
        binding.scoreText.text = "Score: $score"
        binding.timeText.text = "Time: $duration"

        binding.textCountDownForReady.text = "3"
        binding.textCountDownForReady.visibility = View.VISIBLE
        binding.imgClock.visibility = View.VISIBLE

        kotlinView.visibility = View.INVISIBLE

        startCountDownForReady()
    }

    private fun resetViews(){
        score = 0
        binding.scoreText.text = "Score: $score"
        binding.imgClock.visibility = View.VISIBLE
        binding.timeText.text = "Time: $duration"
        kotlinView.visibility = View.INVISIBLE
        binding.textPressToPlay.visibility = View.VISIBLE
    }

    fun setButtonEnabled(boolean: Boolean) {
        if (boolean) {
            binding.btnStartGame.isEnabled = true
            binding.btnBack.isEnabled = true
        } else {
            binding.btnStartGame.isEnabled = false
            binding.btnBack.isEnabled = false
        }
    }

    fun showGameOverDialog() {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Game Over")
        alert.setMessage("Restart the game?")

        alert.setPositiveButton("Yes") { _, _ ->
            restartGame()
        }
        alert.setNegativeButton("No"){ _, _ ->
            resetViews()
        }

        alert.show()
    }

}