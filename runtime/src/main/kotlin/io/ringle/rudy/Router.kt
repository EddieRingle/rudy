package io.ringle.rudy

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import java.util.ArrayList
import java.util.HashMap

public trait Router {

    companion object {

        public val ACTION_ROUTE: String = "io.ringle.rudy.ACTION_ROUTE"
    }

    public val routes: HashMap<String, (Context, RouteResolution) -> Routable?>

    public open fun defaultRouteAction(ctx: Context, state: RouteResolution): Routable? = null

    public fun getRouteAction(key: String): (Context, RouteResolution) -> Routable? {
        return routes.get(key) ?: { ctx, state -> defaultRouteAction(ctx, state) }
    }

    /*
     * Matches a key like:
     *   /path/to/{username}
     * to a path specified as:
     *   /path/to/eddie
     */
    private fun matchPaths(key: String, path: String): Bundle? {
        if (!key[0].equals('/') || !path[0].equals('/')) {
            throw IllegalArgumentException("Paths must lead with a '/'")
        }
        /* Encode key Uri to save the special bits */
        val keyUri = Uri.parse(Uri.encode(key, "/"))
        /* Path uri should already come encoded/URI-compliant */
        val pathUri = Uri.parse(path)
        val keySegs = keyUri.getPathSegments()
        val pathSegs = pathUri.getPathSegments()
        if (keySegs.size() != pathSegs.size()) {
            return null
        }
        val result = Bundle()
        var matches = true
        val merged = keySegs.zip(pathSegs)
        for ((k, p) in merged) {
            if (k.first() == '{' && k.last() == '}') {
                val varying = k.subSequence(1, k.length() - 1).toString()
                if (varying.contains(":")) {
                    val varyingSplit = varying.split(':')
                    if (varyingSplit.size() != 2) {
                        Log.e("Router", "Invalid path parameter specified: $varying")
                        return null
                    }
                    val varType = varyingSplit[0].charAt(0)
                    val varName = varyingSplit[1]
                    when (varType) {
                        'b' -> result.putBoolean(varName, p.toBoolean())
                        'd' -> result.putDouble(varName, p.toDouble())
                        'i' -> result.putInt(varName, p.toInt())
                        's' -> result.putString(varName, p)
                        else -> result.putString(varName, p)
                    }
                } else {
                    result.putString(varying, p)
                }
                continue
            }
            if (!k.equals(p)) {
                matches = false
                break
            }
        }
        if (matches) {
            for (q in pathUri.getQueryParameterNames()) {
                result.putString(q, pathUri.getQueryParameter(q))
            }
        }
        return if (matches) result else null
    }

    public fun resolvePath(path: String): RouteResolution? {
        val matches = ArrayList<RouteResolution>()
        for ((key, route) in routes) {
            val bundle = matchPaths(key, path)
            if (bundle != null) {
                matches.add(RouteResolution(path, key, bundle))
            }
        }
        return matches.sortBy { it.key }.firstOrNull()
    }

    public fun route(path: CharSequence): RouteHelper = RouteHelper(this, path)

    public fun on(path: CharSequence): RouteHelper = RouteHelper(this, path)

    public fun CharSequence.to(fn: (String, Bundle) -> Boolean) {
        this@Router.route(this).to(fn)
    }
}

inline public fun <T> T.configure(fn: T.() -> Unit): T {
    this.fn()
    return this
}

public open class RouteHelper(public val router: Router, public val path: CharSequence) {

    public fun to(fn: (Context, RouteResolution) -> Routable?) {
        router.routes.put(path.toString(), fn)
    }

    public fun to(delegate: Router) {
        router.routes.put(path.toString()) { ctx, res ->
            delegate.dispatcher.dispatchRoute(ctx,
                    res,
                    delegate.getRouteAction(res.key))
        }
    }
}
