package in.medhajnews.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import in.medhajnews.app.R;
import in.medhajnews.app.data.ArticleDBHelper;
import in.medhajnews.app.data.DataManager;
import in.medhajnews.app.data.api.models.Story;
import in.medhajnews.app.data.objects.DataItem;
import in.medhajnews.app.ui.adapters.MainPagerAdapter;
import in.medhajnews.app.ui.fragments.MainFragment;
import in.medhajnews.app.util.Notification;

public class NewsActivity extends AppCompatActivity
        implements View.OnClickListener,
        ViewPager.OnPageChangeListener,
        TabLayout.OnTabSelectedListener {

    private final static int ANIM_DURATION_TOOLBAR = 300;
    private final static int ANIM_DURATION_FAB = 400;
    private final static int INTENT_DELAY = 300;

   //todo : implement sorting and mashing

    private final static String TAG = NewsActivity.class.getSimpleName();


    private MainPagerAdapter mMainPagerAdapter;
    public static TabLayout mTabLayout;
    private ProgressBar mProgress;
    private TextView brokenConnection;
    private ViewPager mViewPager;

    private Snackbar mSnackBar;

    public static FloatingActionMenu mFloatingActionMenu;
    public static AppBarLayout mAppBarLayout;
    private ImageView mToolbarImage;

    private DataManager dataManager;
    private List<DataItem> items;

    public ArticleDBHelper articleDB;


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            startIntroAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        dataManager.cancelLoading();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        brokenConnection = (TextView) findViewById(R.id.connection_error);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarImage = (ImageView) findViewById(R.id.app_logo);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam);
        FloatingActionButton mShareStoryFab = (FloatingActionButton) findViewById(R.id.fab_share_story);
        FloatingActionButton mSavedFab = (FloatingActionButton) findViewById(R.id.fab_saved);
        ImageView mSearchItem = (ImageView) findViewById(R.id.search);

        mSavedFab.setColorNormal(ContextCompat.getColor(this, R.color.fab_dark));
        mSavedFab.setColorPressed(ContextCompat.getColor(this, R.color.fab_dark));
        mShareStoryFab.setColorNormal(ContextCompat.getColor(this, R.color.fab_light));
        mShareStoryFab.setColorPressed(ContextCompat.getColor(this, R.color.fab_light));


        mFloatingActionMenu.getMenuIconView().setImageResource(R.drawable.ic_settings_fab);
        mFloatingActionMenu.setClosedOnTouchOutside(true);

        dataManager = new DataManager(this) {
            @Override
            public void onDataLoaded(List<? extends DataItem> data) {
                items.addAll(data);

            }

            @Override
            protected void loadFinished() {
                //todo : fix crash here, on partial data load
                long seed = System.nanoTime();
                Collections.shuffle(items, new Random(seed)); //todo : sort properly
                fetchCurrentFragment().newsCardAdapter.mLoadList.addAll(0, items);
                fetchCurrentFragment().newsCardAdapter
                        .notifyItemRangeInserted(0, items.size());
                notifyPreLoadedFragment();
                mProgress.setVisibility(View.GONE);
            }

            @Override
            protected void loadStarted() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void loadFailed() {
                showDeviceOffline();
            }
        };
        dataManager.loadData();

        //todo : redo
        mSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsActivity.this, SearchActivity.class));
            }
        });

        mFloatingActionMenu.setIconToggleAnimatorSet(getFamAnimationSet());
        if (mToolbarImage != null) {
            Glide.with(this).load(R.drawable.newslogo).into(mToolbarImage);
        }
        setSupportActionBar(toolbar);
        items = new ArrayList<DataItem>();
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), items);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.spacing_normal));

        articleDB = new ArticleDBHelper(this);
    }

    private void startIntroAnimation() {
        mFloatingActionMenu.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.fab_size_normal));

        mAppBarLayout.setTranslationY(-108);
        mToolbarImage.setTranslationY(-100);

        mAppBarLayout.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        mToolbarImage.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mSnackBar == null) { //an instance of snackBar means no connection
                            mFloatingActionMenu.animate()
                                    .translationY(0)
                                    .setInterpolator(new OvershootInterpolator(1.f))
                                    .setStartDelay(300)
                                    .setDuration(ANIM_DURATION_FAB)
                                    .start();
                        }
                    }
                })
                .start();
    }

    @Override
    public void onBackPressed() {
        if (mFloatingActionMenu.isOpened()) {
            mFloatingActionMenu.close(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(NewsActivity.this, SettingsActivity.class));
        }
        else if (id == R.id.action_feedback) {
            Intent feedback = new Intent(Intent.ACTION_SEND)
                    .setType("text/email")
                    .putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.feedback_email)})
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
            startActivity(Intent.createChooser(feedback, getString(R.string.send_feedback)));
        }
        else if (id == R.id.action_login) {
            startActivity(new Intent(NewsActivity.this, SettingsActivity.class));
        }
        else if (id == R.id.action_rate) {
            //intent will start working after app is published
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("market://details?id=" + getPackageName())));
        }
        else if (id == R.id.action_notify) { // todo : remove after demo
            Notification.notifyUser(NewsActivity.this, (Story) items.get(0), 0);
            if (fetchCurrentFragment().newsCardAdapter != null) {
                fetchCurrentFragment().addItem((Story) items.get(0), 1);
            }
        }
        return true;
    }

    private MainFragment fetchCurrentFragment() {
        return ((MainFragment) mMainPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem()));
    }

    private MainFragment fetchNextFragment() {
        return ((MainFragment) mMainPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem() + 1));
    }

    private MainFragment fetchPreviousFragment() {
        return ((MainFragment) mMainPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem() - 1));
    }

    /* Notifies fragments that Adapter preloaded in memory of new data */
    private void notifyPreLoadedFragment() {
        MainFragment nextFragment, previousFragment;
        nextFragment = fetchNextFragment();
        previousFragment = fetchPreviousFragment();
        if (nextFragment != null) nextFragment.newsCardAdapter.notifyDataSetChanged();
        if (previousFragment != null) previousFragment.newsCardAdapter.notifyDataSetChanged();
    }

    private void showDeviceOffline() {
        //show broken connection, only if the item list is empty
        if(fetchCurrentFragment().newsCardAdapter.mLoadList.size() == 0) {
            brokenConnection.setVisibility(View.VISIBLE);
        }
        mProgress.setVisibility(View.GONE);
        mSnackBar = Snackbar.make(
                mFloatingActionMenu,
                "No Connection",
                Snackbar.LENGTH_INDEFINITE).
                setAction(
                        "Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                brokenConnection.setVisibility(View.GONE); //todo animate this
                                dataManager.loadData();
                            }
                        })
                .setActionTextColor(ContextCompat.getColor(NewsActivity.this, R.color.fab_light));
        mSnackBar.show();
    }


    private AnimatorSet getFamAnimationSet() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(),
                "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(),
                "scaleY", 1.0f, 0.2f);
        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(),
                "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(),
                "scaleY", 0.2f, 1.0f);
        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);
        scaleInX.setDuration(150);
        scaleInY.setDuration(150);
        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFloatingActionMenu.getMenuIconView().setImageResource(mFloatingActionMenu.isOpened()
                        ? R.drawable.ic_settings_fab : R.drawable.ic_close);
            }
        });
        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));
        return set;
    }

    /* Click Listener */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fab_saved: {
                mFloatingActionMenu.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(NewsActivity.this, SavedArticlesActivity.class));
                    }
                }, INTENT_DELAY);
            }

            case R.id.fab_share_story: {
                mFloatingActionMenu.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(NewsActivity.this, ShareActivity.class));
                    }
                }, INTENT_DELAY);
            }

        }
    }

    /* Temporary */
    private void Mashup() {

    }

    /* ViewPager PageScroll Listener */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    /* TabLayout selection listener */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (mFloatingActionMenu.isOpened()) {
            mFloatingActionMenu.close(true);
            onTabSelected(tab);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
