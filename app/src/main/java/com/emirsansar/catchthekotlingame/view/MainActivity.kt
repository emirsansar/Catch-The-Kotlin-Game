package com.emirsansar.catchthekotlingame.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var navHostFragment: NavHostFragment?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentMainContainer) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomMainMenu, navHostFragment!!.navController)
    }


}