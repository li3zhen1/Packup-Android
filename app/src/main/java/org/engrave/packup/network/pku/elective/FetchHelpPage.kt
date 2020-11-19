package org.engrave.packup.network.pku.elective

import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.scanAsString
import java.net.HttpURLConnection
import java.net.URL

internal const val PkuHelperPageUrl =
    "https://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/help/HelpController.jpf"

fun fetchElectiveHelperPage(electiveCookie: DummyCookie): String =
    (URL(PkuHelperPageUrl + ";jsessionid=" + electiveCookie.getItem("JSESSIONID"))
        .openConnection() as HttpURLConnection).run {
        setRequestProperty("Cookie", electiveCookie.toString())
        inputStream.scanAsString()
    }