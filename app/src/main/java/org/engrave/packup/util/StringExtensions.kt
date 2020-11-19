package org.engrave.packup.util

import org.jsoup.Jsoup
import ws.vinta.pangu.Pangu
import java.io.InputStream
import java.util.*
import javax.inject.Inject

fun String.getSpaced(pangu: Pangu): String {
    return pangu.spacingText(this)
}

private val xmlClosureRegex = Regex("""<.*>""")
fun String.cleanXmlNodes() = Jsoup.parse(this).text()

// String extension, convert response to string
fun String.fromInputStream(inputStream: InputStream): String =
    Scanner(inputStream).useDelimiter("\\A").run {
        if (hasNext()) next()
        else ""
    }

fun InputStream.scanAsString(): String = Scanner(this).useDelimiter("\\A").run {
    if (hasNext()) next()
    else ""
}

/**
 * Returns a substring after the first occurrence of delimiterBefore,
 * before the first occurrence of delimiterAfter.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun String.substringBetween(delimiterBefore: String, delimiterAfter: String): String =
    this.substringAfter(delimiterBefore, "").substringBefore(delimiterAfter, "")
