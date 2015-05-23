package io.ringle.rudy

import android.view.*
import android.view.accessibility.AccessibilityEvent

public open class WindowCallbackWrapper(val base: Window.Callback) : Window.Callback {

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return base.dispatchKeyEvent(event)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent): Boolean {
        return base.dispatchKeyShortcutEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return base.dispatchTouchEvent(event)
    }

    override fun dispatchTrackballEvent(event: MotionEvent): Boolean {
        return base.dispatchTrackballEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent?): Boolean {
        return base.dispatchGenericMotionEvent(event)
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean {
        return base.dispatchPopulateAccessibilityEvent(event)
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return base.onCreatePanelView(featureId)
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu?): Boolean {
        return base.onCreatePanelMenu(featureId, menu)
    }

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu?): Boolean {
        return base.onPreparePanel(featureId, view, menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        return base.onMenuOpened(featureId, menu)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        return base.onMenuItemSelected(featureId, item)
    }

    override fun onWindowAttributesChanged(attrs: WindowManager.LayoutParams?) {
        return base.onWindowAttributesChanged(attrs)
    }

    override fun onContentChanged() {
        return base.onContentChanged()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        return base.onWindowFocusChanged(hasFocus)
    }

    override fun onAttachedToWindow() {
        return base.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        return base.onDetachedFromWindow()
    }

    override fun onPanelClosed(featureId: Int, menu: Menu?) {
        return base.onPanelClosed(featureId, menu)
    }

    override fun onSearchRequested(): Boolean {
        return base.onSearchRequested()
    }

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?): ActionMode? {
        return base.onWindowStartingActionMode(callback)
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        return base.onActionModeStarted(mode)
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        return base.onActionModeFinished(mode)
    }
}
