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

class GameManager(private val context: Context, private val duration: String, private val binding: ActivityGameBinding) {

    private var score: Int = 0
    private var randomIndex: Int = 0
    private var imageArray = ArrayList<ImageView>()
    private var runnable = Runnable {}
    private var handler = Handler(Looper.getMainLooper())

    private var viewModel = ViewModelProvider(context as GameActivity)[UserRecordViewModel::class.java]

    init {
        loadGameSettings()
        initViews()
    }


    fun startGame() {
        setButtonEnabled(false)
        showKotlinView()
        startCountDown()
    }

    fun initViews() {
        imageArray.add(binding.imageView)
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
                randomIndex = random.nextInt(9)
                imageArray[randomIndex].visibility = View.VISIBLE

                handler.postDelayed(runnable, 500)
            }
        }
        handler.post(runnable)
    }

    fun increaseScore() {
        score++
        binding.scoreText.text = "Score: $score"
    }

    fun loadGameSettings() {
        binding.timeText.text = "Time: $duration"

        viewModel.fetchDataFromRoomDB("asd", duration) { userRecord ->
            if (userRecord != null){
                binding.highestScore.text = "Highest Score: ${userRecord.record}"
            } else {
                binding.highestScore.text = "Highest Score: 0"
            }
        }
    }

    //CountDownTimer
    fun startCountDown() {
        object : CountDownTimer((duration.toInt() * 1000 + 500).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val formattedTime = String.format("%02d", millisUntilFinished / 1000)
                binding.timeText.text = "Time: $formattedTime"
            }

            override fun onFinish() {
                binding.timeText.text = "The game has ended."
                handler.removeCallbacks(runnable)
                imageArray[randomIndex].visibility = View.INVISIBLE

                updateUserRecord()

                setButtonEnabled(true)
                showGameOverDialog()
            }
        }.start()
    }

    private fun updateUserRecord(){
        viewModel.fetchDataFromRoomDB("asd", duration){ userRecord ->
            if (userRecord != null){
                if (userRecord.record.toInt() < score){
                    binding.highestScore.text = "Highest Score: ${score}"
                    viewModel.updateDataToRoomDB("asd",duration,score.toString())
                }
            } else {
                viewModel.insertDataToRoomDB("asd", duration, score.toString())
                binding.highestScore.text = "Highest Score: $score"
            }
        }
    }



//    fun updateHighScore(score: Int) {
//        binding.highestScore.text = "Highest Score: ${SharedPreferencesManager.getInstance().getHighestScore(duration.toInt())}"
//
//        // Toast.makeText(context, "New Record!", Toast.LENGTH_LONG).show() // This line cannot be accessed directly, needs to be shown from Activity
//    }

    fun restartGame() {
        score = 0
        binding.scoreText.text = "Score: $score"

        loadGameSettings()
        startGame()
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


    fun setListeners(){
        binding.imageView.setOnClickListener {
            increaseScore()
        }
        binding.imageView2.setOnClickListener {
            increaseScore()
        }
        binding.imageView3.setOnClickListener {
            increaseScore()
        }
        binding.imageView4.setOnClickListener {
            increaseScore()
        }
        binding.imageView5.setOnClickListener {
            increaseScore()
        }
        binding.imageView6.setOnClickListener {
            increaseScore()
        }
        binding.imageView7.setOnClickListener {
            increaseScore()
        }
        binding.imageView8.setOnClickListener {
            increaseScore()
        }
        binding.imageView9.setOnClickListener {
            increaseScore()
        }
    }
}