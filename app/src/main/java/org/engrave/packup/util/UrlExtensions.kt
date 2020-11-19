package org.engrave.packup.util

import java.lang.StringBuilder
import java.net.URL
import java.net.URLEncoder

fun makeURL(baseUrl: String, requestKeys: Map<String, String?>): URL {
    val url = StringBuilder(baseUrl)
    url.append("?")
    for (reqKey in requestKeys) {
        url.append(
            URLEncoder.encode(reqKey.key, "UTF-8") + "=" +
                    URLEncoder.encode(reqKey.value ?: "", "UTF-8") + "&"
        )
    }
    return URL(url.substring(0, url.length - 1))
}