package in.medhajnews.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.medhajnews.app.data.api.models.Comment;
import in.medhajnews.app.data.api.models.Story;

/**
 * Created by bhav on 6/11/16 for the Medhaj News Project.
 */
public class ArticleDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ArticleDBHelper.class.getSimpleName();

    // UPDATE VERSION after changing schema
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "medhaj.db";

    /**
     * Story Table (for offline viewing)
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
     * Check {@link in.medhajnews.app.data.Priority} for Category priority.
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
    //todo : limit size of database
    private final static String USER_TABLE_NAME = "user";
    private final static String ARTICLE_TABLE = "articles";

    // Story table Fields
    private final static String COLUMN_ID = "id";
    private final static String COLUMN_NEWS_LINK = "link";
    private final static String COLUMN_IMAGE_LINK = "image_link";
    private final static String COLUMN_TITLE = "title";
    private final static String COLUMN_AUTHOR = "author";
    private final static String COLUMN_DATE = "date";
    private final static String COLUMN_TIME = "time";
    private final static String COLUMN_AREA = "area";
    private final static String COLUMN_CATEGORY = "category";
    private final static String COLUMN_LANGUAGE = "language";
    private final static String COLUMN_CONTENT = "content";


    public ArticleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "ArticleDBHelper called!");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate called");
        String CREATE_ARTICLE_TABLE = "CREATE TABLE " + ARTICLE_TABLE + " ( " +
                COLUMN_ID + " INTEGER NOT NULL UNIQUE, " + COLUMN_LANGUAGE + " TEXT, " +
                COLUMN_IMAGE_LINK + " TEXT, " + COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " + COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " + COLUMN_AUTHOR + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " + COLUMN_NEWS_LINK + " TEXT, " +
                COLUMN_AREA + " TEXT) ";

        sqLiteDatabase.execSQL(CREATE_ARTICLE_TABLE);
    // todo : create some more
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
          * article data is cache of online database
          * change database version -> onUpgrade
         */
        Log.d(TAG, "onUpgrade called");
        db.execSQL("DROP TABLE IF EXISTS " + ARTICLE_TABLE);
    }

    public void SaveArticle(Story story) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        story.isSaved = true;
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, story.id); //0
        cv.put(COLUMN_LANGUAGE, story.language); //1
        cv.put(COLUMN_IMAGE_LINK, story.link_image.get(0));//2
        cv.put(COLUMN_DATE, story.date);//3
        cv.put(COLUMN_TIME, story.time);//4
        cv.put(COLUMN_TITLE, story.title);//5
        cv.put(COLUMN_CONTENT, story.content);//6
        cv.put(COLUMN_AUTHOR, story.author);//7
        cv.put(COLUMN_CATEGORY, story.category);//8
        cv.put(COLUMN_NEWS_LINK, story.url);//9
        cv.put(COLUMN_AREA, story.area);//10

        sqLiteDatabase.insertWithOnConflict(ARTICLE_TABLE, null, cv,
                SQLiteDatabase.CONFLICT_IGNORE); /**
         When a constraint violation occurs, the one row
         that contains the constraint violation is not inserted or changed.*/
        Log.d(TAG, "Inserted story sucessfully");
        sqLiteDatabase.close();
    }

    public ArrayList<Story> getSavedArticles() {
        ArrayList<Story> storyList = new ArrayList<Story>();
        String SELECT_QUERY = "SELECT * FROM " + ARTICLE_TABLE;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);

        List<String> list = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do{
                storyList.add(new Story(
                        cursor.getLong(0),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(5),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(6),
                        cursor.getString(2),
                        list,
                        cursor.getString(10),
                        -1,
                        -1,
                        cursor.getString(1),
                        new ArrayList<Comment>(),
                        "none"
                ));
            } while (cursor.moveToNext());
        }
        if(sqLiteDatabase.isOpen()) {
            cursor.close();
            sqLiteDatabase.close();
        }
        Collections.reverse(storyList);
        return storyList;
    }

    public void DeleteArticle(Story a) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(ARTICLE_TABLE, COLUMN_ID + "=?",new String[] {String.valueOf(a.id)});
        a.isSaved = false;
        if(sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public boolean isArticleSaved(String articleId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String SELECT_QUERY = "SELECT * FROM " + ARTICLE_TABLE + " WHERE "
                + COLUMN_ID + " = " + articleId;
        Cursor c = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
        int i = c.getCount();
        if(sqLiteDatabase.isOpen()) {
            c.close();
            sqLiteDatabase.close();
        }
        return i!=0; //returns true if article is saved
    }
}
