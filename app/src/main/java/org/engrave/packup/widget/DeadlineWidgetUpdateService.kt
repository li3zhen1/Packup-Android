package org.engrave.packup.widget

import org.engrave.packup.data.course.ClassInfoDao
import org.engrave.packup.data.deadline.DeadlineDao
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EventWidgetUpdateService : RemoteViewsService() {
    @Inject
    lateinit var classInfoDao: ClassInfoDao

    @Inject
    lateinit var deadlineDao: DeadlineDao

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        EventWidgetFactory(this.applicationContext, classInfoDao, deadlineDao, intent)
}

class EventWidgetFactory(
    val mContext: Context,
    val classInfoDao: ClassInfoDao,
    val deadlineDao: DeadlineDao,
    val mIntent: Intent?
) : RemoteViewsService.RemoteViewsFactory {

    val mEventWidgetId = mIntent?.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override fun onCreate() {
        TODO("Not yet implemented")
    }

    override fun onDataSetChanged() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getViewAt(position: Int): RemoteViews {

        TODO("Not yet implemented")
    }

    override fun getLoadingView(): RemoteViews {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun hasStableIds(): Boolean {
        TODO("Not yet implemented")
    }

}