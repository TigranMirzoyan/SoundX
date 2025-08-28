package com.soundx.appfragment

import androidx.fragment.app.FragmentTransaction
import com.soundx.R

abstract class SpecialFragment : AppFragment() {
    override fun setupEnterAnimation(transaction: FragmentTransaction) {
        transaction.setCustomAnimations(R.anim.slide_in_up, android.R.anim.fade_out)
    }

    override fun setupExitAnimation(transaction: FragmentTransaction) {
        transaction.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out_down)
    }

    override fun makeVisible() {
        (requireContext() as? SpecialFragmentContainer)?.makeSpecialLayoutVisible()
    }

    override fun getFrameLayoutId(): Int {
        return R.id.special_frameLayout
    }
}