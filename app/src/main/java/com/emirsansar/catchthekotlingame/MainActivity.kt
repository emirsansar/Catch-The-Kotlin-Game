package com.emirsansar.catchthekotlingame

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import com.emirsansar.catchthekotlingame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var timePicker: NumberPicker
    private val timeValues = arrayOf("10", "15", "30")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SharedPreferencesManager.initialize(this)

        initTimePicker()

        timePicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val selectedDuration = timeValues[newVal].toInt()
            SharedPreferencesManager.getInstance().setDuration(selectedDuration)
        }
    }

    fun launchGame(view: View){
        val intent = Intent(this@MainActivity, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun initTimePicker(){
        timePicker = binding.timePicker
        timePicker.minValue = 0
        timePicker.maxValue = timeValues.size - 1
        timePicker.displayedValues = timeValues
        defaultTimePick()
    }

    fun defaultTimePick(){
        SharedPreferencesManager.getInstance().setDuration(10)
    }

    fun quitApp(view: View){
        AlertDialog.Builder(this)
            .setTitle("Quit Game")
            .setMessage("Are you sure you want to exit the game?")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}