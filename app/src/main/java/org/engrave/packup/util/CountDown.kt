package org.engrave.packup.util

import android.os.CountDownTimer

class SimpleCountDown(private val timeInMillis: Long, private val onTimeUp: () -> Unit) :
    CountDownTimer(timeInMillis, 1000) {
    override fun onTick(millisUntilFinished: Long) {}
    override fun onFinish() {
        onTimeUp()
    }
    fun restart(){
        cancel()
        start()
    }
}