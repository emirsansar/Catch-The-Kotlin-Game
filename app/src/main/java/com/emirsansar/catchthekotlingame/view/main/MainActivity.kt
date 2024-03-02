package com.emirsansar.catchthekotlingame.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.databinding.ActivityMainBinding
import com.emirsansar.catchthekotlingame.view.main.HomeFragment
import com.emirsansar.catchthekotlingame.view.main.ProfileFragment
import com.emirsansar.catchthekotlingame.view.main.RankingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var navHostFragment: NavHostFragment?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigationView = binding.bottomMainMenu
        bottomNavigationView.selectedItemId = R.id.empty
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.profileFragment -> {
                    replaceFragment(ProfileFragment())
                    title = "Notification"
                }
                R.id.rankingFragment -> {
                    replaceFragment(RankingFragment())
                    title = "Setting"
                }
            }
            true
        }
        //default fragment
        replaceFragment(HomeFragment())

        setFloatButtonListener()
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentMainContainer,fragment)
            .commit()
    }

    private fun setFloatButtonListener(){
        binding.floatButton.setOnClickListener {
            binding.bottomMainMenu.selectedItemId = R.id.empty
            replaceFragment(HomeFragment())
        }
    }
}