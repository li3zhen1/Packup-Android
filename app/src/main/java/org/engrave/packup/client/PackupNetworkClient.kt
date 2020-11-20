package org.engrave.packup.client

abstract class PackupNetworkClient{
    abstract fun fetchCourseTable()
    abstract fun fetchDeadlineList()
    abstract fun fetchCampusCalendar()
}