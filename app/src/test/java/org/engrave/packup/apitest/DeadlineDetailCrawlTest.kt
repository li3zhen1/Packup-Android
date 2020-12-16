package org.engrave.packup.apitest

import org.engrave.packup.api.pku.course.fetchCourseLoginCookies
import org.engrave.packup.api.pku.course.fetchDeadlineDetailHtml
import org.engrave.packup.pw
import org.engrave.packup.sid
import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.asDocument
import org.engrave.packup.util.attachCookie
import org.engrave.packup.util.scanAsString
import org.junit.Test
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DeadlineDetailCrawlTest {
    @Test
    fun getDetailHtml() {
        val courseLoggedCookie = fetchCourseLoginCookies(
            sid,
            pw
        )
        val doc = fetchDeadlineDetailHtml(
            "_150158_1",
            courseLoggedCookie
        )
        println(doc)
    }

    fun getFileBlobWithCookie(cookie: DummyCookie, url: String, fileName: String) {
        val conn = URL(url).openConnection() as HttpsURLConnection
        conn.apply {
            setRequestProperty("Cookie", cookie.toString())
            requestMethod = "GET"
            doOutput = true
            useCaches = false
            connectTimeout = 100_000
            readTimeout = 100_000
            instanceFollowRedirects = true
        }
        val mimeType = conn.contentType
        val httpStatus = conn.responseCode
        val bis = BufferedInputStream(conn.inputStream)
        val bos = BufferedOutputStream(FileOutputStream("D:/$fileName"))
        var b = 0
        val byArr = ByteArray(1024)
        while (bis.read(byArr).also { b = it } != -1) {
            bos.write(byArr, 0, b)
        }
        bis.close()
        bos.close()
        println("$mimeType $httpStatus")
    }
}