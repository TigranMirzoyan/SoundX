package com.soundx.appfragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

abstract class AppFragment : Fragment() {
    open fun setupEnterAnimation(transaction: FragmentTransaction) {}
    open fun setupExitAnimation(transaction: FragmentTransaction) {}
    abstract fun makeVisible()
    abstract fun getFrameLayoutId(): Int
}