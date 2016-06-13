package in.medhajnews.app.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bhav on 6/11/16 for the Medhaj News Project.
 */
public class ArticleDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ArticleDBHelper.class.getSimpleName();

    // UPDATE VERSION after changing schema
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "medhajarticles.db";

    /**
     * Article Table
     * _____________________________________________________________________________________________________________
     *| ID | Link | Image Link | Title | Author | Date | Update Time | Area | Content | Likes | Dislikes | Comments |
     * -------------------------------------------------------------------------------------------------------------
     *
     *  User Table
     * _____________________________________________
     * | User ID | Name | Email | Facebook | Google |
     * ---------------------------------------------
     *
     */

    private final static String USER_TABLE_NAME = "user";
    private final static String ARTICLES_TABLE_NAME = "articles";

    // Article table Fields
    private final static String COLUMN_ID = "id";
    private final static String COLUMN_LINK = "link";
    private final static String COLUMN_IMAGE_LINK = "image_link";
    private final static String COLUMN_TITLE = "title";
    private final static String COLUMN_AUTHOR = "author";
    private final static String COLUMN_DATE = "date";
    private final static String COLUMN_UPDATE_TIME = "update_time";
    private final static String COLUMN_AREA = "area";
    private final static String COLUMN_CONTENT = "content";
    private final static String COLUMN_LIKES = "likes";
    private final static String COLUMN_DISLIKES = "dislikes";
    private final static String COLUMN_COMMENTS = "comments";

    //todo : User table fielsd

    public ArticleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "ArticleDBHelper called!");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called");
    // todo : create some
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
          * article data is cache of online database
          * change database version -> onUpgrade
         */
        Log.d(TAG, "onUpgrade called");
        db.execSQL("DROP TABLE IF EXISTS " + ARTICLES_TABLE_NAME);
    }
}
