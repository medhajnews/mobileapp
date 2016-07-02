package in.medhajnews.app.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import in.medhajnews.app.ui.ArticleActivity;
import in.medhajnews.app.R;
import in.medhajnews.app.ui.SettingsActivity;
import in.medhajnews.app.data.api.models.Story;
import in.medhajnews.app.external.services.SaveService;

/**
 * Created by bhav on 6/26/16 for the Medhaj News Project.
 * Notifies user.
 */
public class Notification {

    public final static String ID_EXTRA = Notification.class.getSimpleName();

    private static final int[] mId = {1001, 1002, 1003};

    private static final String PRE_TEXT = "Latest: ";
    private static final String PRE_TEXT_IMP = "Exclusive: ";

    private static final String POST_TEXT = ". Find out more";
    private static final String POST_TEXT_IMP = ". Check our coverage of the story";

    private static final long[] VIBRATE_PATTERN = {500, 0, 500, 0};

    private static final int SMALL_ICON = R.drawable.ic_notification;

    private static NotificationManager manager = null;


    public static void notifyUser(Context context, Story story, int notificationCount) {
        //Intents
        //share
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String shareBody = story.title + "\n\n" + story.url;
        String shareSubject = story.title;
        share.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        //story
        Intent contentIntent = new Intent(context, ArticleActivity.class);
        contentIntent.putExtra(Story.INTENT_EXTRA, story);
        //settings
        Intent settings = new Intent(context, SettingsActivity.class);
        //save
        Intent save = new Intent(context, SaveService.class);
        save.putExtra(Story.INTENT_EXTRA, story);
        save.putExtra(ID_EXTRA, mId[notificationCount + 1]);

        //Pending Intents
        PendingIntent shareIntent = PendingIntent.getActivity(context, 0, share, 0);
        PendingIntent intent = PendingIntent.getActivity(context, 0, contentIntent, 0);
        PendingIntent settIntent = PendingIntent.getActivity(context, 0, settings, 0);
        PendingIntent saveIntent = PendingIntent.getService(context, 0, save, PendingIntent.FLAG_CANCEL_CURRENT);

        //Notification Actions
        NotificationCompat.Action Save = new NotificationCompat.Action(0, "Save", saveIntent);
        NotificationCompat.Action Share = new NotificationCompat.Action(0, "Share", shareIntent);
        NotificationCompat.Action Settings = new NotificationCompat.Action(0, "Alert Settings", settIntent);

        //Notification content
        String contentEnd =  notificationCount == 1 ? POST_TEXT_IMP : POST_TEXT;
        String contentStart = notificationCount == 1 ? PRE_TEXT_IMP : PRE_TEXT;
        String message = contentStart + story.title + contentEnd;

        //Expanded Style
        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        inboxStyle.bigText(message);

        manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mNotification = new NotificationCompat.Builder(context)
                .setSmallIcon(SMALL_ICON)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.fab_dark))
                .addAction(Save)
                .addAction(Share)
                .addAction(Settings)
                .setStyle(inboxStyle)
                .setLights(Color.GREEN, 3000, 5000)
                .setVibrate(VIBRATE_PATTERN)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(message);
        inboxStyle.setBuilder(mNotification);
        manager.notify(mId[notificationCount+1], mNotification.build());

    }

    public static void cancelNotification(int notificationId) {
        if(manager!=null && notificationId!=99) {
            manager.cancel(notificationId);
        }
    }
}
