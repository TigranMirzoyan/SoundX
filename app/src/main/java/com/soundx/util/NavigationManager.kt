package com.soundx.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.soundx.R
import com.soundx.view.fragment.PlaylistFragment
import com.soundx.view.fragment.SearchFragment
import com.soundx.view.fragment.SongFragment
import com.soundx.view.fragment.YoutubePlayer

class NavigationManager private constructor(private val fragmentManager: FragmentManager) {
    companion object {
        @Volatile
        private var navigationManager: NavigationManager? = null

        fun instance(fragmentManager: FragmentManager) {
            if (navigationManager != null) return
            synchronized(this) {
                if (navigationManager != null) return
                navigationManager = NavigationManager(fragmentManager)
            }
        }

        fun navigateToFragment(fragment: Enum<*>, bundle: Bundle? = null) {
            requireNotNull(navigationManager) { "${NavigationManager::class.simpleName} isn't initialized" }

            when (fragment) {
                is DefaultFragments -> {
                    navigationManager?.replaceFragment(fragment, bundle)
                    SetSpecialLayoutVisibility.gone()
                }

                is SpecialFragments -> {
                    navigationManager?.replaceFragment(fragment, bundle)
                    SetSpecialLayoutVisibility.visible()
                }

                else -> throw IllegalArgumentException("Unsupported enum type ${fragment::class.simpleName}")
            }
        }
    }

    private val defaultFragmentMap = mapOf(
        DefaultFragments.SONG_FRAGMENT to SongFragment(),
        DefaultFragments.PLAYLIST_FRAGMENT to PlaylistFragment(),
        DefaultFragments.SEARCH_FRAGMENT to SearchFragment(),
    )

    private val specialFragmentMap = mapOf(
        SpecialFragments.YOUTUBE_PLAYER_FRAGMENT to YoutubePlayer()
    )
    private var currentFragment: Fragment

    init {
        fragmentManager.beginTransaction().apply {
            defaultFragmentMap.values.forEach { add(R.id.default_frameLayout, it).hide(it) }
            specialFragmentMap.values.forEach { add(R.id.special_frameLayout, it).hide(it) }
        }.commit()

        currentFragment = defaultFragmentMap[DefaultFragments.SONG_FRAGMENT]!!
        fragmentManager.beginTransaction().show(currentFragment).commit()
    }

    private fun replaceFragment(fragment: DefaultFragments, bundle: Bundle?) {
        val transaction = fragmentManager.beginTransaction()
        val newFragment = defaultFragmentMap[fragment]

        requireNotNull(newFragment) { "Unsupported fragment type ${fragment::class.simpleName}" }
        if (currentFragment == newFragment) return

        transaction.hide(currentFragment)
        newFragment.arguments = bundle
        currentFragment = newFragment

        if (!currentFragment.isAdded) transaction.add(R.id.default_frameLayout, currentFragment)

        transaction.show(currentFragment).commitNow()
    }

    private fun replaceFragment(fragment: SpecialFragments, bundle: Bundle?) {
        val transaction = fragmentManager.beginTransaction()
        val newFragment = specialFragmentMap[fragment]

        requireNotNull(newFragment) { "Unsupported fragment type ${fragment::class.simpleName}" }
        if (currentFragment == newFragment) return

        transaction.hide(currentFragment)
        newFragment.arguments = bundle
        currentFragment = newFragment

        transaction.setCustomAnimations(
            R.anim.slide_in_up,
            R.anim.slide_out_down
        )
        if (!currentFragment.isAdded) transaction.add(R.id.special_frameLayout, currentFragment)

        transaction.show(currentFragment).commitNow()
    }
}