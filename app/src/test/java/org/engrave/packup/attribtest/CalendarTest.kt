package org.engrave.packup.attribtest

import org.engrave.packup.ui.event.semester2020Start
import org.engrave.packup.util.*
import org.junit.Test
import java.util.*

class CalendarTest {
    @Test
    fun testCalendar() {
        with(Calendar.getInstance()) {
            println("${getYear()}-${getMonth()}-${getDate()} - ${getDayOfWeek()}")
        }
        val cld = Calendar.getInstance().apply {
            timeInMillis = semester2020Start

        }
        val clnow = Calendar.getInstance().apply {
            timeInMillis = semester2020Start
        }
        for(i in -10..7) {
            Calendar.getInstance().apply {
                timeInMillis = semester2020Start
                timeInMillis += DAY_IN_MILLIS * i
                print("${getYear()}-${getMonth()}-${getDate()} - ${getDayOfWeek()} ====")
                val timeStart = getWeekStart()
                with(Calendar.getInstance().apply {
                    timeInMillis = timeStart
                }){
                    println("${getYear()}-${getMonth()}-${getDate()} - ${getDayOfWeek()}")
                }
            }
        }
    }

    @Test
    fun extTest(){
        println(("20210112上午"))
    }
}