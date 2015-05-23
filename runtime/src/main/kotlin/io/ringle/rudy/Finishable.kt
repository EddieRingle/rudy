package io.ringle.rudy

public trait Finishable : Resumable {

    public var isFinished: Boolean
        get() = state.getBoolean(Rudy.sKeyFinished, false)
        private set(b) = state.putBoolean(Rudy.sKeyFinished, b)

    public fun finish() {
        isFinished = true
        if (isResumed) {
        }
    }
}
