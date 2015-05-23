package io.ringle.rudy

public trait Navigable {

    public fun onBackPressed(): Boolean {
        return false
    }

    public fun onUpPressed(): Boolean {
        return false
    }
}
