package com.emirsansar.catchthekotlingame.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.NumberPicker
import com.emirsansar.catchthekotlingame.SharedPreferencesManager
import com.emirsansar.catchthekotlingame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var duration: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SharedPreferencesManager.initialize(this)

        setSpinnerListener()
    }

    fun launchGame(view: View){
        val intent = Intent(this@MainActivity, GameActivity::class.java)
        intent.putExtra("DURATION", duration)
        startActivity(intent)
        finish()
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



    fun quitApp(view: View) {
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