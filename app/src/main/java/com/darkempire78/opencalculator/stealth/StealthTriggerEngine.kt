package com.darkempire78.opencalculator.stealth

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.Button

class StealthTriggerEngine(
    private val getInputText: () -> String,
    private val onTrigger: () -> Unit
) {
    private var isButtonPressed = false
    private var pressStartTime = 0L

    companion object {
        private const val LONG_PRESS_DURATION_MS = 1500L
        private const val TARGET_INPUT = "2084"
    }

    private val handler = Handler(Looper.getMainLooper())
    private var triggerRunnable: Runnable? = null

    fun attachToButton(button: Button) {
        button.setOnTouchListener { view, event ->
            handleTouch(event)
            // We return false to let the normal click listener (onClick in XML) still work
            false
        }
    }

    private fun handleTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isButtonPressed = true
                pressStartTime = SystemClock.elapsedRealtime()
                
                triggerRunnable?.let { handler.removeCallbacks(it) }
                triggerRunnable = Runnable {
                    if (isButtonPressed) {
                        validateAndTrigger()
                    }
                }
                handler.postDelayed(triggerRunnable!!, LONG_PRESS_DURATION_MS)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isButtonPressed = false
                cancelTrigger()
            }
        }
    }

    private fun validateAndTrigger() {
        val rawInput = getInputText()
        
        // Sanitize: remove grouping separators (commas/spaces)
        val sanitizedInput = rawInput.filter { it.isDigit() }
        
        if (sanitizedInput == TARGET_INPUT) {
            onTrigger()
        }
    }

    fun cancelTrigger() {
        triggerRunnable?.let { handler.removeCallbacks(it) }
        triggerRunnable = null
    }

    fun detach() {
        cancelTrigger()
        isButtonPressed = false
    }
}
