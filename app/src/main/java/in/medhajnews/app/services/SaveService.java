package in.medhajnews.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import in.medhajnews.app.data.ArticleDBHelper;
import in.medhajnews.app.data.models.Story;
import in.medhajnews.app.utils.NotificationUtils;

/**
 * Created by bhav on 6/27/16 for the Medhaj News Project.
 * Service to save article passed on from a Notification.
 */
public class SaveService extends Service {

    private static final String TAG = SaveService.class.getSimpleName();


    public SaveService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Story mStory = intent.getParcelableExtra(Story.INTENT_EXTRA);
            ArticleDBHelper articleDB = new ArticleDBHelper(this);
            if (!mStory.isSaved) {
                articleDB.SaveArticle(mStory);
            }
            Toast.makeText(SaveService.this, "Story Saved Successfully", Toast.LENGTH_SHORT).show();
            NotificationUtils.cancelNotification(intent.getIntExtra(NotificationUtils.ID_EXTRA, 99));
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
