package in.medhajnews.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.medhajnews.app.services.SaveService;

/**
 * Created by bhav on 6/27/16 for the Medhaj News Project.
 */
public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SaveService.class));
    }
}
