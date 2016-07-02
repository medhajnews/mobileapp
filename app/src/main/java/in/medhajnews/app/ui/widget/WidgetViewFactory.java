package in.medhajnews.app.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import in.medhajnews.app.R;

/**
 * Created by bhav on 6/29/16 for the Medhaj News Project.
 */
public class WidgetViewFactory implements RemoteViewsService.RemoteViewsFactory{

    private static final String[] items={"lorem", "ipsum", "dolor",
            "sit", "amet", "consectetuer",
            "adipiscing", "elit", "morbi",
            "vel", "ligula", "vitae",
            "arcu", "aliquet", "mollis",
            "etiam", "vel", "erat",
            "placerat", "ante",
            "porttitor", "sodales",
            "pellentesque", "augue",
            "purus"};

    private Context mContext = null;
    private int mAppWidgetId;

    public WidgetViewFactory(Context context, Intent intent) {
        this.mContext = context;
        this.mAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews row=new RemoteViews(mContext.getPackageName(),
                R.layout.news_widget_row);

        row.setTextViewText(android.R.id.text1, items[i]);

        Intent intent =new Intent();
        Bundle extras=new Bundle();

        extras.putString(NewsWidgetProvider.WIDGET_EXTRA , items[i]);
        intent.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, intent);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.news_widget_row_loading);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
