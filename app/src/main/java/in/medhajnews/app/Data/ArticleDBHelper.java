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

    private static final String DATABASE_NAME = "medhaj.db";

    /**
     * Article Table (for offline viewing)
     * ___________________________________________________________________________________________
     *| ID | Link | Image Link | Title | Author | Date | Update Time | Area | Content | Photo Art |
     * -------------------------------------------------------------------------------------------
     *
     *
     *  User Table (for user details, 1 row)
     * _____________________________________________
     * | User ID | Name | Email | Facebook | Google |
     * ---------------------------------------------
     *
     *
     * Category Frequency will be stored in the Shared Preferences as Category-Frequency pairs
     *
     *
     * Recommendation Table (for recommended section, 11 rows)
     *
     * ______________________________
     * | Category | Rank | Important |
     * ------------------------------
     *
     * Ranking is based on simple frequency of category viewing. The recommendation table will hold
     * only the top 4 categories and display 10 articles. The category rank will be updated every
     * time an article is viewed. If an article is viewed from the recommended section the rank will
     * not be updated.
     *
     * Since the ranking is based on simple frequency, it is possible for different categories to
     * have equal ranks. In such cases Category priority will decide the the ranking.
     *
     * Check {@link in.medhajnews.app.Data.Priority} for Category priority.
     *
     * Rank 0 - Breaking NEWS
     * Rank 1 - 4 of the newest articles from the category
     * Rank 2 - 3 articles
     * Rank 3 - 2 articles
     * Rank 4 - 1 article
     *
     * The recommendation table will also have a importance column. Only very important news will
     * be the 11th article in the recommendation table. This article will be placed first in the
     * recommended section for a select time period.
     */

    //todo : intent and broadcast receivers
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
