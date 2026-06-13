package com.darkempire78.opencalculator.stealth

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.Button

class StealthTriggerEngine(
    private val getResultDisplayText: () -> String,
    private val onTrigger: () -> Unit
) {
    private var isButtonPressed = false
    private var pressStartTime = 0L

    companion object {
        private const val LONG_PRESS_DURATION_MS = 1500L // Slightly longer for a single button to avoid accidents
        private const val TARGET_RESULT = "2084"
    }

    private val handler = Handler(Looper.getMainLooper())
    private var triggerRunnable: Runnable? = null

    fun attachToButton(button: Button) {
        button.setOnTouchListener { view, event ->
            handleTouch(event)
            if (event.action == MotionEvent.ACTION_UP) {
                view.performClick()
            }
            false // Retain false so normal calculator button functions still trigger
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
        val rawResult = getResultDisplayText()
        
        // 🛡️ Robust Check: Strip out commas, decimals, trailing zeros, or spaces
        // If rawResult is "2,084.00" -> sanitized becomes "2084"
        val sanitizedResult = rawResult.split(".")[0].filter { it.isDigit() }
        
        if (sanitizedResult == TARGET_RESULT) {
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
