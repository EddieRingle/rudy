package io.ringle.rudy

import android.content.Context
import io.ringle.statesman.Contextual

public val Context.router: RouteManager
        get() = getSystemService(RudyContextWrapper.ROUTER_SERVICE) as RouteManager

public val Contextual.router: RouteManager
        get() = ctx.getSystemService(RudyContextWrapper.ROUTER_SERVICE) as RouteManager
