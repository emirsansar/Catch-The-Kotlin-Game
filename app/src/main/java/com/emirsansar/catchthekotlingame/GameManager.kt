package com.emirsansar.catchthekotlingame

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.emirsansar.catchthekotlingame.databinding.ActivityGameBinding
import com.emirsansar.catchthekotlingame.view.GameActivity
import com.emirsansar.catchthekotlingame.viewmodel.UserRecordViewModel
import java.util.Random

class GameManager(private val context: Context, private val userEmail: String, private val duration: String, private val binding: ActivityGameBinding) {

    private var score: Int = 0
    private var userHighestScore: Int = 0
    private var randomIndex: Int = 0
    private var imageArray = ArrayList<ImageView>()
    private var runnable = Runnable {}
    private var handler = Handler(Looper.getMainLooper())

    private var viewModel = ViewModelProvider(context as GameActivity)[UserRecordViewModel::class.java]

    init {
        viewModel.getUserScoreFromFirestore(userEmail, duration){
            binding.highestScore.text = "Highest Score: "+ it.toString()
        }
        initViews()
    }


    fun startGame() {
        setButtonEnabled(false)
        startCountDownForReady()
    }

    fun initViews() {
        imageArray.add(binding.imageView1)
        imageArray.add(binding.imageView2)
        imageArray.add(binding.imageView3)
        imageArray.add(binding.imageView4)
        imageArray.add(binding.imageView5)
        imageArray.add(binding.imageView6)
        imageArray.add(binding.imageView7)
        imageArray.add(binding.imageView8)
        imageArray.add(binding.imageView9)
        hideViews()
        setListeners()
    }

    fun hideViews() {
        for (image in imageArray) {
            image.visibility = View.INVISIBLE
        }
    }

    fun showKotlinView() {
        runnable = object : Runnable {
            override fun run() {
                imageArray[randomIndex].visibility = View.INVISIBLE

                val random = Random()
                var nextRandomIndex = random.nextInt(9)
                while (nextRandomIndex == randomIndex) {
                    nextRandomIndex = random.nextInt(9)
                }
                randomIndex = nextRandomIndex

                imageArray[randomIndex].visibility = View.VISIBLE

                handler.postDelayed(runnable, 500)
            }
        }
        handler.post(runnable)
    }

    private fun increaseScore() {
        score++
        binding.scoreText.text = "Score: $score"
    }


    //CountDownTimer
    fun startCountDownInPlaying() {
        object : CountDownTimer((duration.toInt() * 1000 + 500).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val formattedTime = String.format("%02d", millisUntilFinished / 1000)
                binding.timeText.text = "Time: $formattedTime"
            }

            override fun onFinish() {
                binding.timeText.text = "The game has ended."
                handler.removeCallbacks(runnable)
                imageArray[randomIndex].visibility = View.INVISIBLE

                checkHighestScore(score)

                setButtonEnabled(true)
                showGameOverDialog()
            }
        }.start()
    }

    fun checkHighestScore(score: Int){
        if (score > userHighestScore){
            binding.highestScore.text = "Highest Score: $score"
            viewModel.setUserScoreToFirestore(userEmail, duration, score)
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
        binding.scoreText.text = "Score: 0"
        binding.timeText.text = "$duration"

        binding.textCountDownForReady.text = "3"
        binding.textCountDownForReady.visibility = View.VISIBLE

        startCountDownForReady()
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
                binding.gridLayout.visibility = View.VISIBLE
            }
        }.start()
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
        alert.setNegativeButton("No", null)

        alert.show()
    }


    private fun setListeners(){
        for (imageView in imageArray) {
            imageView.setOnClickListener {
                increaseScore()
            }
        }
    }

    private fun showNewImageView() {
        val random = Random()
        val lastIndex = randomIndex
        while (randomIndex == lastIndex) {
            randomIndex = random.nextInt(9)
        }
        imageArray[randomIndex].visibility = View.VISIBLE
    }
}