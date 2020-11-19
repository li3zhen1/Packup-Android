package org.engrave.packup.network.pku.course

import kotlinx.serialization.Serializable

@Serializable
data class DeadlineRawJson (
    var allDay: Boolean,
    var itemSourceId: String,
    var repeat: Boolean,
    var calendarId: String,
    //var recur: Boolean,
    var calendarName: String,
    var attemptable: Boolean,
    var id: String,
    //var start: String,
    //var end: String,
    var title: String,
    //var startDate: String,
    var endDate: String,
    var eventType: String
)

@Serializable
data class CalendarNameLocalizable(
    val rawValue: String
)
