package com.soundx.viewmodel

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.soundx.R
import com.soundx.view.fragment.HomeFragment
import com.soundx.view.fragment.LibraryFragment
import com.soundx.view.fragment.SearchFragment

class NavigationManager(private val fragmentManager: FragmentManager) {

    companion object {
        private var navigationManager: NavigationManager? = null

        fun instance(fragmentManager: FragmentManager) {
            if (navigationManager == null) navigationManager = NavigationManager(fragmentManager)
        }

        fun navigateToFragment(fragment: Fragments, bundle: Bundle? = null) {
            navigationManager?.replaceFragment(fragment, bundle)
        }
    }

    private val fragmentMap = mapOf(
        Fragments.HOME_FRAGMENT to HomeFragment(),
        Fragments.LIBRARY_FRAGMENT to LibraryFragment(),
        Fragments.SEARCH_FRAGMENT to SearchFragment()
    )
    private var currentFragment: Fragment = fragmentMap[Fragments.HOME_FRAGMENT]!!

    init {
        fragmentManager.beginTransaction().apply {
            fragmentMap.values.forEach { add(R.id.frameLayout, it).hide(it) }
        }.commit()
        fragmentManager.beginTransaction().show(currentFragment).commit()
    }

    fun replaceFragment(fragmentType: Fragments, bundle: Bundle?) {
        val newFragment = fragmentMap[fragmentType]
        newFragment?.arguments = bundle

        if (currentFragment == newFragment || newFragment == null) return

        val transaction = fragmentManager.beginTransaction()
        transaction.hide(currentFragment)
        currentFragment = newFragment

        if (!currentFragment.isAdded) transaction.add(R.id.frameLayout, currentFragment)
        transaction.show(currentFragment).commitNow()
    }
}