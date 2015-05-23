package io.ringle.rudy

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

public open class RudyContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {
        public val ROUTER_SERVICE: String = "io.ringle.rudy.ROUTER_SERVICE"
    }

    private val manager = RouteManager.create()

    override fun getSystemService(name: String): Any? =
        if (!ROUTER_SERVICE.equals(name)) {
            super.getSystemService(name)
        } else {
            manager
        }
}
