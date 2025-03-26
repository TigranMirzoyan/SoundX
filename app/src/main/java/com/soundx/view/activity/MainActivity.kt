package com.soundx.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.soundx.R
import com.soundx.databinding.ActivityMainBinding
import com.soundx.util.Fragments
import com.soundx.util.NavigationManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationManager.instance(supportFragmentManager)
        setupNavigationBar()
    }

    private fun setupNavigationBar() {
        binding.navigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> NavigationManager.navigateToFragment(Fragments.HOME_FRAGMENT)
                R.id.library -> NavigationManager.navigateToFragment(Fragments.LIBRARY_FRAGMENT)
                R.id.search -> NavigationManager.navigateToFragment(Fragments.SEARCH_FRAGMENT)
            }
            true
        }
    }
}