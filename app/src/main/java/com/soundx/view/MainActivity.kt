package com.soundx.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.soundx.R
import com.soundx.databinding.ActivityMainBinding
import com.soundx.util.NavigationManager
import com.soundx.appfragment.SpecialFragmentContainer
import com.soundx.view.fragment.main.PlaylistFragment
import com.soundx.view.fragment.main.SearchFragment
import com.soundx.view.fragment.main.SongFragment

class MainActivity : AppCompatActivity(), SpecialFragmentContainer {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        NavigationManager.init(supportFragmentManager)
        setContentView(binding.root)

        var keyboardWasVisible = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { view, insets ->
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            if (isKeyboardVisible) {
                view.updatePadding(bottom = view.paddingBottom)
                keyboardWasVisible = true
            } else {
                view.updatePadding(bottom = if (keyboardWasVisible) view.paddingBottom else view.paddingBottom + navBarInsets)
                keyboardWasVisible = false
            }
            insets
        }

        setupBackButtonClick()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.song -> NavigationManager.navigateToFragment(SongFragment::class)
                R.id.playlist -> NavigationManager.navigateToFragment(PlaylistFragment::class)
                R.id.search -> NavigationManager.navigateToFragment(SearchFragment::class)
            }
            true
        }
    }

    private fun setupBackButtonClick() {
        this.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavigationManager.navigateToFragment(SongFragment::class)
                binding.bottomNavigation.selectedItemId = R.id.song
            }
        })
    }

    override fun setSpecialLayoutVisibility(state: Boolean) {
        binding.specialFrameLayout.visibility = if (state) View.VISIBLE else View.GONE
        binding.bottomNavigation.visibility = if (!state) View.VISIBLE else View.GONE
    }
}