package org.engrave.packup.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineRepository
import org.engrave.packup.data.persistence.ApplicationPreferenceConfigsRepository
import org.engrave.packup.util.*
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
                    unfinished.filter { it.due_time != null && it.due_time > 0 && it.due_time <= weekEnd }.size
                unfinished.size.toString() + context.getString(R.string.widget_recently_remaining) +
                        numDue + context.getString(R.string.widget_recently_due_this_week)
            } else {
                if(unfinished.isEmpty())
                    context.getString(R.string.widget_recently_relax)
                else unfinished.size.toString() + context.getString(R.string.widget_recently_remaining) +
                        numDue + context.getString(R.string.widget_recently_due_tomorrow)
            }

            appWidgetIds.forEach { appWidgetId ->
                val views: RemoteViews = RemoteViews(
                    context.packageName,
                    R.layout.recently_widget
                ).apply {
                    setTextViewText(
                        R.id.widget_all_deadline_number,
                        unfinished.size.toString()
                    )
                    setTextViewText(
                        R.id.widget_deadline_digest,
                        displayString
                    )
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