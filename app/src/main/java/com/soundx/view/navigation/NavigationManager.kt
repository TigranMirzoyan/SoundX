package com.soundx.view.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.soundx.R
import com.soundx.util.DialogFragments
import com.soundx.util.Fragments
import com.soundx.view.fragment.library.CreatePlaylistFragment
import com.soundx.view.fragment.home.HomeFragment
import com.soundx.view.fragment.library.LibraryFragment
import com.soundx.view.fragment.dialog.PlaylistDialogFragment
import com.soundx.view.fragment.library.EditPlaylistFragment
import com.soundx.view.fragment.search.SearchFragment

class NavigationManager(private val fragmentManager: FragmentManager) {

    companion object {
        private var navigationManager: NavigationManager? = null

        fun instance(fragmentManager: FragmentManager) {
            if (navigationManager == null) navigationManager = NavigationManager(fragmentManager)
        }

        fun navigateToFragment(fragment: Enum<*>, bundle: Bundle? = null) {
            when (fragment) {
                is Fragments -> navigationManager?.replaceFragment(fragment, bundle)
                is DialogFragments -> navigationManager?.showDialogFragment(fragment, bundle)
                else -> throw IllegalArgumentException("Unsupported enum type: ${fragment::class.simpleName}")
            }
        }
    }

    private val fragmentMap = mapOf(
        Fragments.HOME_FRAGMENT to HomeFragment(),
        Fragments.LIBRARY_FRAGMENT to LibraryFragment(),
        Fragments.SEARCH_FRAGMENT to SearchFragment(),
        Fragments.CREATE_PLAYLIST_FRAGMENT to CreatePlaylistFragment(),
        Fragments.EDIT_PLAYLIST_FRAGMENT to EditPlaylistFragment()
    )

    private val dialogFragmentMap = mapOf(
        DialogFragments.PLAYLIST_DIALOG_FRAGMENT to PlaylistDialogFragment()
    )

    private var currentFragment: Fragment = fragmentMap[Fragments.HOME_FRAGMENT]!!

    init {
        fragmentManager.beginTransaction().apply {
            fragmentMap.values.forEach { add(R.id.frameLayout, it).hide(it) }
        }.commit()
        fragmentManager.beginTransaction().show(currentFragment).commit()
    }

    private fun replaceFragment(fragment: Fragments, bundle: Bundle?) {
        val newFragment = fragmentMap[fragment]
        newFragment?.arguments = bundle

        if (currentFragment == newFragment || newFragment == null) return

        val transaction = fragmentManager.beginTransaction()
        transaction.hide(currentFragment)
        currentFragment = newFragment

        if (!currentFragment.isAdded) transaction.add(R.id.frameLayout, currentFragment)
        transaction.show(currentFragment).commitNow()
    }

    private fun showDialogFragment(fragment: DialogFragments, bundle: Bundle?) {
        val newDialog = dialogFragmentMap[fragment]
        newDialog?.arguments = bundle
        newDialog?.show(fragmentManager, "$fragment")
    }
}