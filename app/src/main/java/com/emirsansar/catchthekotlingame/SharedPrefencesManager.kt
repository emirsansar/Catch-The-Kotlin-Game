package com.emirsansar.catchthekotlingame

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("com.emirsansar.catchthekennykotlin", Context.MODE_PRIVATE)

    companion object {
        private var instance: SharedPreferencesManager? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = SharedPreferencesManager(context)
            }
        }

        fun getInstance(): SharedPreferencesManager {
            if (instance == null) {
                throw IllegalStateException("SharedPreferencesManager must be initialized")
            }
            return instance as SharedPreferencesManager
        }
    }

    fun setHighestScore(highestScore: Int, duration: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("highestScore${duration}", highestScore)
        editor.apply()
    }

    fun getHighestScore(duration: Int): Int {
        return sharedPreferences.getInt("highestScore${duration}", 0)
    }

    fun setDuration(duration: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("duration", duration)
        editor.apply()
    }

    fun getDuration(): Int {
        return sharedPreferences.getInt("duration", 10)
    }

    fun setLastScore(lastScore: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("lastScore", lastScore)
        editor.apply()
    }

    fun getLastScore(): Int {
        return sharedPreferences.getInt("lastScore", 0)
    }
}