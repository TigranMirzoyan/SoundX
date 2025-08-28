package com.soundx.util

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.soundx.appfragment.AppFragment
import com.soundx.view.fragment.PlaylistFragment
import com.soundx.view.fragment.SearchFragment
import com.soundx.view.fragment.SongFragment
import com.soundx.view.fragment.YoutubePlayer
import kotlin.reflect.KClass

class NavigationManager private constructor(private val fragmentManager: FragmentManager) {
    private val fragmentMap = listOf(
        SongFragment(),
        PlaylistFragment(),
        SearchFragment(),
        YoutubePlayer(),
    ).associateBy { it::class }
    private var currentFragmentKey: KClass<out AppFragment> = SongFragment::class

    init {
        fragmentManager.beginTransaction().apply {
            fragmentMap.values.forEach { fragment ->
                add(fragment.getFrameLayoutId(), fragment).hide(fragment)
            }
            show(fragmentMap.getValue(currentFragmentKey))
        }.commit()
    }

    private fun switchFragment(newFragmentKey: KClass<out AppFragment>, bundle: Bundle?) {
        if (currentFragmentKey == newFragmentKey) return

        val currentFragment = fragmentMap.getValue(currentFragmentKey)
        val newFragment = fragmentMap.getValue(newFragmentKey).apply { arguments = bundle }

        fragmentManager.beginTransaction().apply {
            newFragment.setupEnterAnimation(this)
            currentFragment.setupExitAnimation(this)
            hide(currentFragment).show(newFragment)
        }.commit()

        currentFragmentKey = newFragmentKey
        newFragment.makeVisible()
    }

    companion object {
        @Volatile
        private var instance: NavigationManager? = null

        fun init(fragmentManager: FragmentManager) {
            synchronized(this) {
                if (instance != null) return
                instance = NavigationManager(fragmentManager)
            }
        }

        fun navigateToFragment(fragment: KClass<out AppFragment>, bundle: Bundle? = null) {
            instance?.switchFragment(fragment, bundle)
                ?: error("${NavigationManager::class.simpleName} is not initialized.")
        }
    }
}