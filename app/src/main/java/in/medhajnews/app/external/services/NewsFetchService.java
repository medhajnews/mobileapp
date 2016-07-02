package in.medhajnews.app.external.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by bhav on 6/24/16 for the Medhaj News Project.
 */
public class NewsFetchService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
