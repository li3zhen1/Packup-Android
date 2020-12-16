package org.engrave.packup.util


import java.io.Serializable

class DummyCookie private constructor(
    private val cookieMap: Map<String, String>
) : Serializable {
    companion object {
        fun makeCookie(key: String, value: String) = DummyCookie(mapOf(key to value))

        fun makeCookie(cookies: Map<String, String>) = DummyCookie(cookies)

        fun makeCookie(cookiesHeader: List<String>?) =
            if (cookiesHeader != null)
                makeCookie(
                    cookiesHeader.associateBy(
                        { it.substringBefore("=") },
                        { it.substringAfter("=").substringBefore(";") }
                    )
                )
            else makeCookie(mapOf())

        fun mergeCookieWithHeader(dummyCookie: DummyCookie, cookiesHeader: List<String>?) =
            if (cookiesHeader != null && cookiesHeader.isNotEmpty())
                DummyCookie(
                    dummyCookie.cookieMap +
                            cookiesHeader.associateBy(
                                { it.substringBefore("=") },
                                { it.substringAfter("=").substringBefore(";") }
                            )
                )
            else dummyCookie
    }


    override fun toString(): String {
        val cookieListString = StringBuilder("")
        for (cookieItem in cookieMap) {
            cookieListString.append(cookieItem.key + "=" + cookieItem.value + "; ")
        }
        return cookieListString.toString().substringBeforeLast(";")
    }

    fun toString(vararg keys: String): String {
        val cookieListString = StringBuilder("")
        for (cookieKey in keys) {
            if (cookieKey in cookieMap) {
                cookieListString.append(cookieKey + "=" + cookieMap[cookieKey] + "; ")
            }
        }
        return cookieListString.toString().substringBeforeLast(";")
    }

    fun getItem(key: String) = cookieMap[key]
}