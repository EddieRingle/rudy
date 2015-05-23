package io.ringle.rudy

import android.widget.FrameLayout
import io.ringle.statesman.Stateful

public trait Resumable : Stateful {

    public var isResumed: Boolean
        get()  = state.getBoolean(Rudy.sKeyResumed, false)
        private set(b) = state.putBoolean(Rudy.sKeyResumed, b)

    fun dispatchOnPause() {
        isResumed = false
        onPause()
    }

    fun dispatchOnResume() {
        isResumed = true
        onResume()
    }

    public fun onPause() {
    }

    public fun onResume() {
    }
}
