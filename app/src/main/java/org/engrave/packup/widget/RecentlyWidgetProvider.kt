package org.engrave.packup.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.engrave.packup.R
import org.engrave.packup.SplashActivity
import org.engrave.packup.data.deadline.DeadlineRepository
import org.engrave.packup.data.persistence.ApplicationPreferenceConfigsRepository
import org.engrave.packup.util.asLocalCalendar
import org.engrave.packup.util.setToEndOfTomorrow
import org.engrave.packup.util.setToEndOfWeek
import org.engrave.packup.util.toGlobalizedString
import java.util.*
import javax.inject.Inject


/**
 * Implementation of Recent Deadlines Widget functionality.
 */
@AndroidEntryPoint
class RecentlyWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var deadlineRepository: DeadlineRepository

    @Inject
    lateinit var configsRepository: ApplicationPreferenceConfigsRepository


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        GlobalScope.launch {
            val allDeadlinesStatic = withContext(Dispatchers.IO) {
                deadlineRepository.allDeadlinesStatic
            }
            val unfinished = allDeadlinesStatic.filter {
                !it.is_completed && !it.is_deleted
            }.sortedBy {
                it.due_time
            }
            // TODO: 时区问题
            val tomorrowEnd = Calendar.getInstance().setToEndOfTomorrow().timeInMillis
            val weekEnd = Calendar.getInstance().setToEndOfWeek(
                configsRepository.getPreferenceConfigs().useSaturdayAsEndOfWeek
            ).timeInMillis
            var numDue = unfinished.filter {
                it.due_time != null && it.due_time > 0 && it.due_time <= tomorrowEnd
            }.size
            val displayString = if (numDue == 0) {
                numDue =
                    unfinished.filter { it.due_time != null && it.due_time > Date().time && it.due_time <= weekEnd }.size
                unfinished.size.toString() + context.getString(R.string.widget_recently_remaining) +
                        numDue + context.getString(R.string.widget_recently_due_this_week)
            } else {
                if(unfinished.isEmpty())
                    context.getString(R.string.widget_recently_relax)
                else unfinished.size.toString() + context.getString(R.string.widget_recently_remaining) +
                        numDue + context.getString(R.string.widget_recently_due_tomorrow)
            }

            val intent = Intent(context, SplashActivity::class.java)
            val pending = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_NO_CREATE
            )

            appWidgetIds.forEach { appWidgetId ->
                val views: RemoteViews = RemoteViews(
                    context.packageName,
                    R.layout.recently_widget
                ).apply {
                    setOnClickPendingIntent(
                        R.id.widget_container,
                        pending
                    )
                    setTextViewText(
                        R.id.widget_all_deadline_number,
                        unfinished.size.toString()
                    )
                    setTextViewText(
                        R.id.widget_deadline_digest,
                        displayString
                    )
                    if(unfinished.isNotEmpty()) {
                        setTextViewText(
                            R.id.ddl1,
                            (unfinished.elementAtOrNull(0)?.name) ?: ""
                        )
                        setTextViewText(
                            R.id.due1,
                            (unfinished.elementAtOrNull(0)?.due_time?.asLocalCalendar()
                                ?.toGlobalizedString(
                                    context,
                                    autoOmitYear = true,
                                    omitTime = false,
                                    omitWeek = true
                                )) ?: ""
                        )
                        setTextViewText(
                            R.id.course1,
                            (unfinished.elementAtOrNull(0)?.source_course_name_without_semester)
                                ?: ""
                        )
                        setViewVisibility(R.id.star1, if(unfinished[0].is_starred)View.VISIBLE else View.GONE)
                    }else{
                        setViewVisibility(R.id.item1, View.GONE)
                    }

                    if(unfinished.size>1) {
                        setTextViewText(
                            R.id.ddl2,
                            (unfinished.elementAtOrNull(1)?.name) ?: ""
                        )
                        setTextViewText(
                            R.id.due2,
                            (unfinished.elementAtOrNull(1)?.due_time?.asLocalCalendar()
                                ?.toGlobalizedString(
                                    context,
                                    autoOmitYear = true,
                                    omitTime = false,
                                    omitWeek = true
                                )) ?: ""
                        )
                        setTextViewText(
                            R.id.course2,
                            (unfinished.elementAtOrNull(1)?.source_course_name_without_semester)
                                ?: ""
                        )
                        setViewVisibility(R.id.star2, if(unfinished[1].is_starred)View.VISIBLE else View.GONE)
                    }else{
                        setViewVisibility(R.id.item2, View.GONE)
                    }
                    if(unfinished.size>2) {
                        setTextViewText(
                            R.id.ddl3,
                            (unfinished.elementAtOrNull(2)?.name) ?: ""
                        )
                        setTextViewText(
                            R.id.due3,
                            (unfinished.elementAtOrNull(2)?.due_time?.asLocalCalendar()
                                ?.toGlobalizedString(
                                    context,
                                    autoOmitYear = true,
                                    omitTime = false,
                                    omitWeek = true
                                )) ?: ""
                        )
                        setTextViewText(
                            R.id.course3,
                            (unfinished.elementAtOrNull(2)?.source_course_name_without_semester)
                                ?: ""
                        )
                        setViewVisibility(R.id.star3, if(unfinished[2].is_starred)View.VISIBLE else View.GONE)
                    } else setViewVisibility(R.id.item3, View.GONE)
                }
                AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views)
            }
        }
    }

/*        context.showToast("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        appWidgetIds.forEach { appWidgetId ->
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.recently_widget
            ).apply {
                setTextViewText(R.id.widget_all_deadline_number, deadlineRepository.deadlines.value?.size.toString())
            }
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        val updateWorkRequest = OneTimeWorkRequestBuilder<RecentlyWidgetUpdateWorker>()
            .setInputData(
                workDataOf(
                    KEY_TARGET_APPWIDGET_IDS to appWidgetIds
                )
            ).build()
        WorkManager.getInstance(context).enqueue(updateWorkRequest)
        try {
            PendingIntent.getService(context,)
            PendingIntent(context, RecentlyWidgetUpdateService::class.java).let {
                context.startForegroundService(it)
                Thread.sleep(1000)
                context.stopService(it)
            }
        } catch (e: Exception) {
            context.showToast(e.message.toString())
        }*/

    /**
     * relevant functionality for when the first widget is enabled
     */
    override fun onEnabled(context: Context) {

    }

    /**
     * relevant functionality for when the last widget is disabled
     */
    override fun onDisabled(context: Context) {
    }

}
/*
@AndroidEntryPoint
class RecentlyWidgetUpdateService : Service() {
    @Inject
    lateinit var classInfoRepository: ClassInfoRepository
    override fun onBind(intent: Intent?): IBinder? {
        //showToast("onBind()")
        return null
    }
    override fun onStart(intent: Intent?, startId: Int) {
        GlobalScope.launch {
            // IO Scope
            val allClassInfoNum = classInfoRepository.allClassInfoNum
            withContext(Dispatchers.Main) {
                this@RecentlyWidgetUpdateService.let { service ->
                    //showToast("${service::classInfoRepository.isInitialized} $allClassInfoNum")
                    val remoteWidgetViews =
                        RemoteViews(service.packageName, R.layout.recently_widget).apply {
                            setTextViewText(
                                R.id.widget_all_deadline_number,
                                allClassInfoNum.toString()
                            )
                        }
                    val thisWidget =
                        ComponentName(service, RecentlyWidgetProvider::class.java)
                    val manager = AppWidgetManager.getInstance(service)
                    manager.updateAppWidget(thisWidget, remoteWidgetViews)
                }
            }
        }
    }
}
private const val KEY_TARGET_APPWIDGET_IDS = "KEY_TARGET_APPWIDGET_ID"
// TODO: https://stackoverflow.com/questions/46179256/how-to-properly-update-a-widget-in-android-8-0-oreo-api-26
class RecentlyWidgetUpdateWorker @WorkerInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workParams: WorkerParameters,
    private var classInfoRepository: ClassInfoRepository,
) : Worker(
    appContext,
    workParams
) {
    override fun doWork(): Result {
        val appWidgetIds = inputData.getIntArray(KEY_TARGET_APPWIDGET_IDS)
        val allClassInfoNum = classInfoRepository.allClassInfoNum
        appWidgetIds?.forEach { appWidgetId ->
            val views: RemoteViews = RemoteViews(
                appContext.packageName,
                R.layout.recently_widget
            ).apply {
                setTextViewText(R.id.widget_all_deadline_number, allClassInfoNum.toString())
            }
            AppWidgetManager.getInstance(appContext).updateAppWidget(appWidgetId, views)
        }
        return Result.success()
    }
}*/