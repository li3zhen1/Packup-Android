package org.engrave.packup.data.course

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.engrave.packup.data.account.AccountInfoRepository
import org.engrave.packup.network.pku.elective.fetchElectiveLoginCookies
import org.engrave.packup.network.pku.elective.fetchElectiveResultTable
import org.engrave.packup.network.pku.portal.Semester
import org.engrave.packup.network.pku.portal.SemesterSeason
import org.engrave.packup.network.pku.portal.fetchPortalCourseInfo
import org.engrave.packup.network.pku.portal.fetchPortalLoginCookies
import javax.inject.Inject

class ClassInfoRepository @Inject constructor(
    private val classInfoDao: ClassInfoDao,
    private val accountInfoRepository: AccountInfoRepository
) {
    val allClassInfo: LiveData<List<ClassInfo>> = classInfoDao.getAll()
    val allClassInfoNum get()= classInfoDao.getAllStatic().size

    init {

    }

    private suspend fun getAllClassInfoNum() = withContext(Dispatchers.IO){
        classInfoDao.getAllStatic().size
    }


    private suspend fun crawlSpecifiedClassInfoFromPortal(
        semester: Semester
    ) = withContext(Dispatchers.IO) {
        val accountInfo = accountInfoRepository.getAccountInfo()
        val rawJson = fetchPortalCourseInfo(
            semester,
            fetchPortalLoginCookies(accountInfo.studentId, accountInfo.password)
        )
        ClassInfo.fromCourseRawJson(rawJson, semester).forEach {
            classInfoDao.insertOrUpdate(it)
        }
    }

    private suspend fun crawlCurrentClassInfoFromElective() = withContext(Dispatchers.IO) {
        val accountInfo = accountInfoRepository.getAccountInfo()
        val rawHtml = fetchElectiveResultTable(
            fetchElectiveLoginCookies(accountInfo.studentId, accountInfo.password)
        )
        ClassInfo.fromElectiveResultHtml(rawHtml).forEach {
            classInfoDao.insertOrUpdate(it)
        }
    }


    suspend fun crawlAllClassInfo() {
        crawlCurrentClassInfoFromElective()
        crawlSpecifiedClassInfoFromPortal(
            Semester(
                2019,
                SemesterSeason.SPRING
            )
        )
        crawlSpecifiedClassInfoFromPortal(
            Semester(
                2019,
                SemesterSeason.SUMMER
            )
        )
    }

    suspend fun insertClassInfo(classInfoDao: ClassInfoDao) {

    }

}