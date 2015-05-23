package io.ringle.rudy

import io.ringle.statesman.Stateful

public trait Attachable : Stateful {

    public var isAttached: Boolean
        get()  = state.getBoolean(Rudy.sKeyAttached)
        private set(b) = state.putBoolean(Rudy.sKeyAttached)

    fun dispatchOnAttach() {
        isAttached = true
        onAttach()
    }

    fun dispatchOnDetach() {
        isAttached = false
        onDetach()
    }

    public fun onAttach() {
    }

    public fun onDetach() {
    }
}
