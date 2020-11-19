package org.engrave.packup.network.pku.elective

import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.scanAsString
import java.net.HttpURLConnection
import java.net.URL

internal const val PkuElectiveResultTableUrl =
    "https://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/electiveWork/showResults.do"

fun fetchElectiveResultTable(electiveCookie: DummyCookie): String =
    (URL(PkuElectiveResultTableUrl).openConnection() as HttpURLConnection).run {
        setRequestProperty("Cookie", electiveCookie.toString())
        setRequestProperty("Referer", PkuHelperPageUrl)
        inputStream.scanAsString()
    }