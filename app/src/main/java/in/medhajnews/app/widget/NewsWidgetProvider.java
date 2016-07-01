package in.medhajnews.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import in.medhajnews.app.NewsActivity;
import in.medhajnews.app.R;
import in.medhajnews.app.SettingsActivity;

/**
 * Created by bhav on 6/29/16 for the Medhaj News Project.
 * AppWidgetProvider is an extension of Broadcast Receiver for receiving
 *
 *      ACTION_APPWIDGET_UPDATE   onUpdate()
 *      ACTION_APPWIDGET_DELETED    onDeleted()
 *      ACTION_APPWIDGET_ENABLED    onEnabled()
 *      ACTION_APPWIDGET_DISABLED   onDisabled()
 *      ACTION_APPWIDGET_OPTIONS_CHANGED    onAppWidgetOptionsChanged()
 */
public class NewsWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_EXTRA = "Widget_id";
    public NewsWidgetProvider() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            Intent news = new Intent(context, NewsActivity.class);
            PendingIntent pendingNews = PendingIntent.getActivity(context, 0, news, 0);

            Intent settings = new Intent(context, SettingsActivity.class);
            PendingIntent pendingSetting = PendingIntent.getActivity(context, 0, settings, 0);

//            Intent refresh = new Intent(context, WidgetRefresh.class);
//            PendingIntent pendingRefresh = PendingIntent.getActivity(context, 0, refresh, 0);

//            Intent list = new Intent(context, ArticleActivity.class);
//            list.putExtra(WIDGET_EXTRA, appWidgetId);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.news_widget);
//            views.setOnClickPendingIntent(R.id.app_logo, pendingNews);
//            views.setOnClickPendingIntent(R.id.refresh, pendingRefresh);
//            views.setOnClickPendingIntent(R.id.settings, pendingSetting);

//            views.setRemoteAdapter(appWidgetId, R.id.widget_list, listIntent);

            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget=new RemoteViews(context.getPackageName(),
                    R.layout.news_widget);

            widget.setRemoteAdapter(appWidgetId, R.id.widget_list,
                    svcIntent);

            Intent clickIntent=new Intent(context, NewsActivity.class);
            PendingIntent clickPI=PendingIntent
                    .getActivity(context, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.widget_list, clickPI);



            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);

            super.onUpdate(context, appWidgetManager, appWidgetIds);
        }

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}
