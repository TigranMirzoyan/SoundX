package com.soundx.appfragment

import android.os.Handler
import android.os.Looper
import com.soundx.R

abstract class DefaultFragment : AppFragment() {
    private val animationDurationMillis = 300L

    override fun makeVisible() {
        (requireContext() as? SpecialFragmentContainer)?.let { container ->
            Handler(Looper.getMainLooper()).postDelayed({
                container.makeSpecialLayoutGone()
            }, animationDurationMillis)
        }
    }

    override fun getFrameLayoutId(): Int {
        return R.id.default_frameLayout
    }
}