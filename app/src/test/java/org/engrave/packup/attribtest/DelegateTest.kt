package org.engrave.packup.attribtest

import org.junit.Test

class DelegateTest {
    @Test
    fun byLazyTest(){
        val a by lazy {
            println("a init")
            1
        }
        val b by lazy {
            println("b init")
            a + 1
        }

        println(b)
    }
}