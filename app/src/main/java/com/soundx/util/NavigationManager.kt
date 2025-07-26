package com.soundx.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.soundx.R
import com.soundx.view.fragment.PlaylistFragment
import com.soundx.view.fragment.SearchFragment
import com.soundx.view.fragment.SongFragment
import com.soundx.view.fragment.ViewMusicVideoFragment

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
            if (navigationManager == null) throw IllegalStateException("NavigationManager isn't initialized")

            when (fragment) {
                is DefaultFragments -> {
                    navigationManager?.replaceFragment(fragment, bundle)
                    SpecialLayoutVisibility.gone()
                }

                is SpecialFragments -> {
                    navigationManager?.replaceFragment(fragment, bundle)
                    SpecialLayoutVisibility.visible()
                }

                else -> throw IllegalArgumentException("Unsupported enum type ${fragment::class.simpleName}")
            }
        }
    }

    private var currentFragment: Fragment

    private val defaultFragmentMap = mapOf(
        DefaultFragments.SONG_FRAGMENT to SongFragment(),
        DefaultFragments.PLAYLIST_FRAGMENT to PlaylistFragment(),
        DefaultFragments.SEARCH_FRAGMENT to SearchFragment(),
    )

    private val specialFragmentMap = mapOf(
        SpecialFragments.VIEW_MUSIC_VIDEO_FRAGMENT to ViewMusicVideoFragment()
    )

    init {
        fragmentManager.beginTransaction().apply {
            defaultFragmentMap.values.forEach { add(R.id.default_frameLayout, it).hide(it) }
            specialFragmentMap.values.forEach { add(R.id.special_frameLayout, it).hide(it) }
        }.commit()

        currentFragment = defaultFragmentMap[DefaultFragments.SONG_FRAGMENT]!!
        fragmentManager.beginTransaction().show(currentFragment).commit()
    }

    private fun <T> replaceFragment(
        fragment: T,
        bundle: Bundle?
    ) where T : Enum<T> {

        val newFragment = when (fragment) {
            is DefaultFragments -> defaultFragmentMap[fragment]
            is SpecialFragments -> specialFragmentMap[fragment]
            else -> null
        }
        newFragment?.arguments = bundle

        if (currentFragment == newFragment || newFragment == null) return

        val transaction = fragmentManager.beginTransaction()
        transaction.hide(currentFragment)
        currentFragment = newFragment

        if (!currentFragment.isAdded) transaction.add(
            if (fragment is SpecialFragments) R.id.special_frameLayout else R.id.default_frameLayout,
            currentFragment
        )
        transaction.show(currentFragment).commitNow()
    }
}