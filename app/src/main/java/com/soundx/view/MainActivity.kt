package com.soundx.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.soundx.R
import com.soundx.databinding.ActivityMainBinding
import com.soundx.util.DefaultFragments
import com.soundx.util.NavigationManager
import com.soundx.util.SetSpecialLayoutVisibility

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        NavigationManager.instance(supportFragmentManager)
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

        SetSpecialLayoutVisibility.init(setSpecialLayoutVisibility())
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.song -> NavigationManager.navigateToFragment(DefaultFragments.SONG_FRAGMENT)
                R.id.playlist -> NavigationManager.navigateToFragment(DefaultFragments.PLAYLIST_FRAGMENT)
                R.id.search -> NavigationManager.navigateToFragment(DefaultFragments.SEARCH_FRAGMENT)
            }
            true
        }
    }

    private fun setSpecialLayoutVisibility(): (Boolean) -> Unit = { bool ->
        binding.specialFrameLayout.visibility = if (bool) View.VISIBLE else View.GONE
        binding.bottomNavigation.visibility = if (!bool) View.VISIBLE else View.GONE
    }
}