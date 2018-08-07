package eu.wonderfulme.triptracker.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.apache.commons.lang3.StringUtils;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

public class TripTrackerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            update(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trip_tracker);
        String routes = UtilsSharedPref.getWidgetRoutes(context);
        if (StringUtils.isEmpty(routes)) {
            remoteViews.setTextViewText(R.id.tv_widget, routes);
        }
    }

    private static void update(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trip_tracker);
        String routes = UtilsSharedPref.getWidgetRoutes(context);
        if (!StringUtils.isEmpty(routes)) {
            remoteViews.setTextViewText(R.id.tv_widget, routes);
        } else {
            remoteViews.setTextViewText(R.id.tv_widget, context.getString(R.string.widget_no_route_available));
        }

        //Click Listener only if app is not recording
        if (!UtilsSharedPref.getWidgetServiceChecker(context)) {
            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.tv_widget, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }
}
