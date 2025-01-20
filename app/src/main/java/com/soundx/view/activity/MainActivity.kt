package com.soundx.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.soundx.R
import com.soundx.databinding.ActivityMainBinding
import com.soundx.viewmodel.Fragments
import com.soundx.viewmodel.MainViewModel
import com.soundx.viewmodel.NavigationManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationManager.instance(supportFragmentManager)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.navigationEvent.observe(this) { fragment ->
            NavigationManager.navigateToFragment(fragment)
        }

        setupNavigationBar()
    }

    private fun setupNavigationBar() {
        binding.navigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> viewModel.navigateTo(Fragments.HOME_FRAGMENT)
                R.id.library -> viewModel.navigateTo(Fragments.LIBRARY_FRAGMENT)
                R.id.search -> viewModel.navigateTo(Fragments.SEARCH_FRAGMENT)
            }
            true
        }
    }
}