package in.medhajnews.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bhav on 6/21/16 for the Medhaj News Project.
 */
public class CacheDBHelper extends SQLiteOpenHelper {

    private Context mContext;

    private static final String TAG = CacheDBHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cache.db";

    /**
     * Story Table
     * _____________________________________________________________________________
     * | Story ID | Language | Image Link | Date | Time | Title | Content | Author |
     * ------------------------------------------------------------------------------
     */

    private final static String ARTICLE_TABLE = "articles";

    private final static String COLUMN_ID = "id";
    private final static String COLUMN_LANGUAGE = "language";
    private final static String COLUMN_IMAGE_LINK = "image_link";
    private final static String COLUMN_DATE = "date";
    private final static String COLUMN_TIME = "time";
    private final static String COLUMN_TITLE = "title";
    private final static String COLUMN_CONTENT = "content";
    private final static String COLUMN_AUTHOR = "author";
    private final static String COLUMN_CATEGORY = "category";
    private final static String COLUMN_LIKES = "likes";
    private final static String COLUMN_DISLIKES = "dislikes";
    private final static String COLUMN_NEWS_LINK = "news_link";
    private final static String COLUMN_AREA = "area";

    public CacheDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ARTICLE_TABLE = "CREATE TABLE " + ARTICLE_TABLE + " ( " +
                COLUMN_ID + " TEXT NOT NULL UNIQUE, " + COLUMN_LANGUAGE + " TEXT, " +
                COLUMN_IMAGE_LINK + " TEXT, " + COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " + COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " + COLUMN_AUTHOR + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " + COLUMN_LIKES + " TEXT, " +
                COLUMN_DISLIKES + " TEXT, " + COLUMN_NEWS_LINK + " TEXT, " +
                COLUMN_AREA + " TEXT) ";

        sqLiteDatabase.execSQL(CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //this database is a cache of the online one , discard everything on upgrades
        dropCache();
    }

    public void dropCache() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ARTICLE_TABLE);
        if(sqLiteDatabase.isOpen()) sqLiteDatabase.close();
    }

    public void purgeCache() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + ARTICLE_TABLE);
        if(sqLiteDatabase.isOpen()) sqLiteDatabase.close();
    }

//    public void addArticle(Story article) {
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//
//        ContentValues cv = new ContentValues();
//        cv.put(COLUMN_ID, article.ArticleId); //0
//        cv.put(COLUMN_LANGUAGE, article.ArticleLanguage); //1
//        cv.put(COLUMN_IMAGE_LINK, article.ArticleImageLink);//2
//        cv.put(COLUMN_DATE, article.ArticleDate);//3
//        cv.put(COLUMN_TIME, article.ArticleUpdateTime);//4
//        cv.put(COLUMN_TITLE, article.ArticleTitle);//5
//        cv.put(COLUMN_CONTENT, article.ArticleContent);//6
//        cv.put(COLUMN_AUTHOR, article.ArticleAuthor);//7
//        cv.put(COLUMN_CATEGORY, article.Category);//8
//        cv.put(COLUMN_LIKES, String.valueOf(article.ArticleLikes));//9
//        cv.put(COLUMN_DISLIKES, String.valueOf(article.ArticleDislikes));//10
//        cv.put(COLUMN_NEWS_LINK, article.ArticleLink);//11
//        cv.put(COLUMN_AREA, article.ArticleArea);//12
//
//        sqLiteDatabase.insertWithOnConflict(ARTICLE_TABLE, null, cv,
//                SQLiteDatabase.CONFLICT_IGNORE); /**
//         When a constraint violation occurs, the one row
//         that contains the constraint violation is not inserted or changed.*/
//        Log.d(TAG, "Inserted article sucessfully");
//        sqLiteDatabase.close();
//    }

//    public ArrayList<Story> getArticles() {
//        ArticleDBHelper helper = new ArticleDBHelper(mContext);
//        ArrayList<Story> articleList = new ArrayList<Story>();
//        String SELECT_QUERY = "SELECT * FROM " + ARTICLE_TABLE;
//        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
//
//
//        if(cursor.moveToFirst()) {
//            do{
//                articleList.add(new Story(
//                        cursor.getString(6),
//                        cursor.getString(7),
//                        cursor.getString(5),
//                        cursor.getString(3),
//                        cursor.getString(4),
//                        cursor.getString(8),
//                        helper.isArticleSaved(cursor.getString(0)), //todo replace with id->savedpost method from dbhelper
//                        cursor.getString(2),
//                        cursor.getString(11),
//                        cursor.getString(12),
//                        6,
//                        9,
//                        cursor.getString(0),
//                        cursor.getString(1)
//                ));
//            } while (cursor.moveToNext());
//        }
//        if(sqLiteDatabase.isOpen()) {
//            cursor.close();
//            sqLiteDatabase.close();
//        }
//        return articleList;
//    }
}
