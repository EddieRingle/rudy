package io.ringle.rudy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.NonNull
import io.ringle.statesman.LifecycleAdapter
import java.util.ArrayDeque
import java.util.HashMap
import kotlin.properties.Delegates

public class RouteManager private() : LifecycleAdapter, Navigable, Router {

    override var target: Activity by Delegates.notNull()

    override val routes = HashMap<String, (Context, RouteResolution) -> Routable?>()

    val handler: Handler

    val backstack = ArrayDeque<RouteRecord>()

    public var routeIntent: Intent? = null

    public var defaultRoute: String? = null

    var lastFinished = false

    companion object {

        public val MSG_GO: Int = 1

        public val MSG_FINISH: Int = 3

        public val MSG_ATTACH: Int = 4

        public val MSG_DETACH: Int = 5

        public val MSG_RESUME: Int = 6

        public val MSG_PAUSE: Int = 7

        public fun create(): RouteManager = RouteManager()
    }

    init {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_GO -> dispatchGo(msg.getData())
                    MSG_BACK -> {
                        lastFinished = false
                        dispatchBack()
                    }
                    MSG_FINISH -> {
                        lastFinished = true
                        dispatchBack()
                    }
                    else -> super.handleMessage(msg)
                }
            }
        }
    }

    fun dispatchGo(args: Bundle) {
        val uri = args.getString("uri")
        val stack = args.getBoolean("stack")
        val arr = backstack.toLinkedList()
        fun findPrior(): Pair<Int, RouteRecord?> {
            backstack.forEachIndexed { i, record ->
                val info = record
                if (info.resolution.uri.toString().equals(uri.toString())) {
                    return Pair(i, record)
                }
            }
            return Pair(-1, null)
        }
        val (priorIndex, priorRecord) = findPrior()
        val res = priorRecord?.resolution ?: resolvePath(uri.toString())
        if (res == null) {
            return
        }
        val rt = dispatcher.dispatchRoute(act, res, getRouteAction(res.key))
        if (rt == null) {
            return
        }
        val newRecord = RouteRecord(rt.key, stack, res)
        newRecord.routable = rt
        dispatcher.dispatchOnAdded(act, rt, res)
        if (priorRecord != null) {
            arr.set(priorIndex, newRecord)
            if (priorIndex > 0) {
                val t = arr.take(priorIndex)
                t.forEach {
                    if (it.routable != null) {
                        dispatcher.dispatchOnPause(act, it.routable!!)
                        dispatcher.dispatchUnroute(act, it.routable!!)
                    }
                    act.statesman.deleteState(it.key)
                }
                arr.removeAll(t)
            }
        } else {
            if (arr.size() < 1 || arr.peek().addToBackstack) {
                arr.push(newRecord)
            } else {
                val top = arr.peek()
                if (top?.routable != null) {
                    dispatcher.dispatchOnPause(act, top?.routable!!)
                    dispatcher.dispatchUnroute(act, top?.routable!!)
                }
                arr.set(0, newRecord)
            }
        }
        backstack.clear()
        backstack.addAll(arr)
        if (rt.finished) {
            back()
        } else {
            dispatcher.dispatchOnResume(act, rt)
            if (arr.size() > 1) {
                val last = arr.get(1)
                if (last.routable != null) {
                    dispatcher.dispatchOnPause(act, last.routable!!)
                }
            }
        }
    }

    public fun go(uri: Uri, stack: Boolean): Boolean {
        val args = Bundle()
        val path = if (uri.getQuery() != null) {
            "${uri.getPath()}?${uri.getQuery()}"
        } else {
            uri.getPath()
        }
        args.putString("uri", path)
        args.putBoolean("stack", stack)
        val msg = Message.obtain(handler, MSG_GO)
        msg.setData(args)
        return handler.sendMessage(msg)
    }

    public fun go(intent: Intent, stack: Boolean): Boolean = go(intent.getData(), stack)

    public fun go(uri: String, stack: Boolean): Boolean = go(Uri.parse(uri), stack)

    fun dispatchBack(): Boolean {
        if (backstack.size() <= 1) {
            return false
        }
        val top = backstack.peek()
        if (top?.routable?.onBackPressed() ?: false) {
            return true
        }
        if (top?.routable != null) {
            dispatcher.dispatchOnPause(act, top.routable!!)
            dispatcher.dispatchUnroute(act, top.routable!!)
        }
        act.statesman.deleteState(top.key)
        backstack.pop()
        return go(backstack.peek().resolution.uri, backstack.peek().addToBackstack)
    }

    public fun back(): Boolean {
        if (backstack.size() <= 1) {
            return false
        }
        val top = backstack.peek()
        if (top?.routable?.onBackPressed() ?: false) {
            return true
        }
        return handler.sendEmptyMessage(MSG_BACK)
    }

    public fun up(): Boolean {
        return false
    }

    public fun onNewIntent(intent: Intent?) {
        routeIntent = intent
    }

    override fun onBackPressed(): Boolean {
        return back()
    }

    override fun onUpPressed(): Boolean {
        return up()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        if (target.getIntent().getDataString()?.isNotEmpty() ?: false) {
            routeIntent = target.getIntent()
        }
        if (savedInstanceState != null) {
            val savedStack: List<RouteRecord>? = savedInstanceState.getParcelableArrayList("__rudy_backstack")
            if (savedStack != null) {
                backstack.addAll(savedStack)
            }
        }
    }

    override fun onActivityPaused() {
        super<LifecycleAdapter>.onActivityPaused()
        for (r in backstack) {
            if (r.routable?.isResumed ?: false) {
                r.routable!!.needsResume = true
                dispatcher.dispatchOnPause(act, r.routable!!)
            }
        }
    }

    override fun onActivityResumed() {
        super<LifecycleAdapter>.onActivityResumed()
        if (routeIntent?.getData() != null) {
            go(routeIntent!!, true)
        } else if (backstack.size() > 0) {
            go(backstack.peek().resolution.uri, backstack.peek().addToBackstack)
        } else if (defaultRoute != null) {
            go(defaultRoute!!, false)
        }
    }

    override fun onActivitySaveInstanceState([NonNull] outState: Bundle) {
        super<LifecycleAdapter>.onActivitySaveInstanceState(outState)
        outState?.putParcelableArrayList("__rudy_backstack", backstack.toArrayList())
    }
}
