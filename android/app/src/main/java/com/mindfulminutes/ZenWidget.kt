package com.mindfulminutes

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * ZenWidget — shows today's Zen quote and intention on the home screen.
 * Tapping the widget launches the app.
 */
class ZenWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = context.getSharedPreferences("mindful_prefs", Context.MODE_PRIVATE)
            val quote = prefs.getString("widget_quote", "The quieter you become, the more you can hear.") ?: ""
            val quoteAuthor = prefs.getString("widget_quote_author", "") ?: ""
            val intention = prefs.getString("daily_intention", "") ?: ""

            val displayQuote = if (quoteAuthor.isNotBlank()) "\u201C$quote\u201D \u2014 $quoteAuthor" else "\u201C$quote\u201D"
            val displayIntention = if (intention.isNotBlank()) intention else "Tap to set your intention for today"

            val views = RemoteViews(context.packageName, R.layout.zen_widget)
            views.setTextViewText(R.id.widget_quote, displayQuote)
            views.setTextViewText(R.id.widget_intention, displayIntention)

            // Launch app on tap
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
