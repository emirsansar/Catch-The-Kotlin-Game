package com.emirsansar.catchthekotlingame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.emirsansar.catchthekotlingame.databinding.ActivityGameBinding
import java.util.Random

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private var score: Int = 0
    private var highestScore: Int = 0
    private var randomIndex: Int = 0
    private var imageArray = ArrayList<ImageView>()
    private var runnable = Runnable {}
    private var handler = Handler(Looper.getMainLooper())
    private var duration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SharedPreferencesManager.initialize(this)

        loadGameSettings()
        initViews()

        binding.btnStartGame.setOnClickListener {
            startGame()
        }
        binding.btnBack.setOnClickListener {
            backToMenu()
        }
    }


    fun startGame(){
        setButtonEnabled(false)
        showKotlinView()
        startCountDown()
    }

    fun initViews(){
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
    }

    fun hideViews(){
        for(image in imageArray){
            image.visibility = View.INVISIBLE
        }
    }

    fun showKotlinView(){
        runnable = object: Runnable{
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

    fun increaseScore(view: View){
        score++
        binding.scoreText.text = "Score: ${score}"
    }

    fun loadGameSettings(){
        duration = SharedPreferencesManager.getInstance().getDuration()
        binding.timeText.text = "Time: ${duration}"

        highestScore = SharedPreferencesManager.getInstance().getHighestScore(duration)
        binding.highestScore.text = "Highest Score: $highestScore"
    }

    //CountDownTimer
    fun startCountDown(){
        object: CountDownTimer( (SharedPreferencesManager.getInstance().getDuration()*1000 + 500).toLong(), 1000){
            override fun onTick(millisUntilFinished: Long) {
                val formattedTime = String.format("%02d", millisUntilFinished/1000)
                binding.timeText.text = "Time: " + formattedTime
            }

            override fun onFinish() {
                binding.timeText.text = "The game has ended."
                handler.removeCallbacks(runnable)
                imageArray[randomIndex].visibility = View.INVISIBLE

                if (SharedPreferencesManager.getInstance().getHighestScore(duration) < score)  updateHighScore(score)

                setButtonEnabled(true)
                showGameOverDialog()
            }
        }.start()
    }

    fun showGameOverDialog() {
        val alert = AlertDialog.Builder(this@GameActivity)
        alert.setTitle("Game Over")
        alert.setMessage("Restart the game?")

        alert.setPositiveButton("Yes") { _, _ ->
            restartGame()
        }
        alert.setNegativeButton("No", null)

        alert.show()
    }

    fun updateHighScore(score: Int){
        SharedPreferencesManager.getInstance().setHighestScore(score, duration)
        binding.highestScore.text = "Highest Score: ${SharedPreferencesManager.getInstance().getHighestScore(duration)}"

        Toast.makeText(this@GameActivity,"New Record!", Toast.LENGTH_LONG).show()
    }

    fun restartGame(){
        score = 0
        binding.scoreText.text = "Score: $score"

        loadGameSettings()
        startGame()
    }

    fun endGame(){
        SharedPreferencesManager.getInstance().setDuration(10)
        val intent = Intent(this@GameActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun backToMenu(){
        android.app.AlertDialog.Builder(this)
            .setTitle("Back To Main Menu")
            .setMessage("Are you sure you want to back the main menu?")
            .setPositiveButton("Yes") { _, _ ->
                endGame()
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun setButtonEnabled(boolean: Boolean){
        if(boolean) {
            binding.btnStartGame.isEnabled = true
            binding.btnBack.isEnabled = true
        } else {
            binding.btnStartGame.isEnabled = false
            binding.btnBack.isEnabled = false
        }
    }
}