package org.engrave.packup.client

import org.engrave.packup.data.account.AccountInfo
import java.util.*
import javax.inject.Inject

class PkuClient(
    accountInfo: AccountInfo
) : PackupNetworkClient() {


    init {
        if(accountInfo.schoolAbbr!= SCHOOL_ABBR){
            throw Exception("School specified doesn't match client type.")
        }

    }

    override fun fetchCourseTable() {
        TODO("Not yet implemented")
    }

    override fun fetchDeadlineList() {
        TODO("Not yet implemented")
    }

    override fun fetchCampusCalendar() {
        TODO("Not yet implemented")
    }

    companion object{
        const val SCHOOL_ABBR = "pku"
    }

}