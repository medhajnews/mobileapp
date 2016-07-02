package in.medhajnews.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import in.medhajnews.app.R;
import in.medhajnews.app.ui.adapters.SaveArticlesAdapter;
import in.medhajnews.app.data.ArticleDBHelper;
import in.medhajnews.app.data.api.models.Story;

public class SavedArticlesActivity extends AppCompatActivity {

    private RecyclerView mArticleRecyclerView;
    private SaveArticlesAdapter mSaveArticlesAdapter;
    private ArrayList<Story> storyList;
    private ArticleDBHelper mSavedArticlesDB;
    private final static String TAG = SavedArticlesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Saved Articles");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mArticleRecyclerView = (RecyclerView) findViewById(R.id.save_recyclerview);
        mArticleRecyclerView.setHasFixedSize(true);
        mSavedArticlesDB = new ArticleDBHelper(this);
        storyList = mSavedArticlesDB.getSavedArticles();
        mSaveArticlesAdapter =
                new SaveArticlesAdapter(storyList, this);
        LinearLayoutManager newLinearLayoutManager = new LinearLayoutManager(this);
        mArticleRecyclerView.setLayoutManager(newLinearLayoutManager);
        mArticleRecyclerView.setAdapter(mSaveArticlesAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        mSaveArticlesAdapter.mSavedStories = mSavedArticlesDB.getSavedArticles();
        mSaveArticlesAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
