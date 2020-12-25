package org.engrave.packup.util.device

import android.app.Activity
import android.app.Service
import android.os.VibrationEffect
import android.os.Vibrator

fun setOffVibration(vibrator: Vibrator, milliseconds: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) =
    vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude))


////以pattern[]方式震动
//fun vibrate(activity: Activity, pattern: LongArray?, repeat: Int) {
//    val vib = activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
//    val vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
//    vib.vibrate(vibrationEffect)
//}

//取消震动
fun vibrateCancel(activity: Activity) {
    val vib = activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    vib.cancel()
}