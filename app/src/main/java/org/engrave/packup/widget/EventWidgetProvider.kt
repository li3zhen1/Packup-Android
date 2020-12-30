package org.engrave.packup.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.engrave.packup.R


internal const val INTENT_OPEN_COURSE_GRID = ""
internal const val INTENT_OPEN_MOST_IMPORTANT_DEADLINE = ""

/**
 * Implementation of Event Widget functionality.
 */
class EventWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val mgr = AppWidgetManager.getInstance(context)
        when (intent?.action) {
            INTENT_OPEN_COURSE_GRID -> TODO()
            INTENT_OPEN_MOST_IMPORTANT_DEADLINE -> TODO()
        }
//        if (intent!!.action.equals(TOAST_ACTION)) {
//            val appWidgetId = intent!!.getIntExtra(
//                AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID
//            )
//            val viewIndex = intent!!.getIntExtra(EXTRA_ITEM, 0)
//            Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
//        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    //Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.event_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)
    //Instruct the widget manager to update the widget

//    val intent = Intent(context, EventWidgetUpdateService::class.java).apply {
//        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
//    }
//    val rv = RemoteViews(context.packageName, R.layout.event_widget).apply {
//        setRemoteAdapter(R.id.appwidget_gridview, intent)
//        setEmptyView(R.id.appwidget_gridview, R.id.appwidget_empty_view)
//    }
//    val onClickGridIntent = Intent(context, EventWidgetProvider::class.java).apply {
//        action = INTENT_OPEN_COURSE_GRID
//        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//    }
//    intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
//    val onClickGridPendingIntent =
//        PendingIntent.getBroadcast(context, 0, onClickGridIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//    rv.setPendingIntentTemplate(R.id.appwidget_gridview, onClickGridPendingIntent)
//    appWidgetManager.updateAppWidget(appWidgetId, rv)
}

class DeadlineWidgetProvider {
}