package eu.wonderfulme.triptracker.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import org.apache.commons.lang3.StringUtils;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

public class TripTrackerWidget extends AppWidgetProvider {
    public static final String ACTION_ROUTES_CHANGED = "ACTION_ROUTES_CHANGED";
    private String mRoutes;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && StringUtils.equals(intent.getAction(), ACTION_ROUTES_CHANGED)) {
            mRoutes = UtilsSharedPref.getWidgetRoutes(context);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            update(context, appWidgetManager, appWidgetId, mRoutes);
        }
    }

    @Override
    public void onEnabled(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trip_tracker);
        String routes = UtilsSharedPref.getWidgetRoutes(context);
        if (routes != null && routes.isEmpty()) {
            remoteViews.setTextViewText(R.id.tv_widget, routes);
        }
    }

    private static void update(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String routes) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trip_tracker);
        if (TextUtils.isEmpty(routes)) {
            routes = UtilsSharedPref.getWidgetRoutes(context);
        }
        remoteViews.setTextViewText(R.id.tv_widget, routes);

        //Click Listener only if app is not recording
        if (!UtilsSharedPref.getWidgetServiceChecker(context)) {
            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.tv_widget, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }
}
