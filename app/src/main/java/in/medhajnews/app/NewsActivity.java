package in.medhajnews.app;

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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import in.medhajnews.app.adapters.MainPagerAdapter;
import in.medhajnews.app.data.models.Story;
import in.medhajnews.app.data.objects.DataItem;
import in.medhajnews.app.fragments.MainFragment;
import in.medhajnews.app.interfaces.DataFetcher;
import in.medhajnews.app.data.models.Advert;
import in.medhajnews.app.utils.NotificationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsActivity extends AppCompatActivity {

    private final static int ANIM_DURATION_TOOLBAR = 300;
    private final static int ANIM_DURATION_FAB = 400;
    private final static int INTENT_DELAY = 300;

    private final static String TAG = NewsActivity.class.getSimpleName();
    private MainPagerAdapter mMainPagerAdapter;
    public static TabLayout mTabLayout;
    private ProgressBar mProgress;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Call<List<Story>> mAPICall;
    private Call<List<Advert>> mAdCall;
    private Snackbar mSnackBar;

    public static FloatingActionMenu mFloatingActionMenu;
    public static AppBarLayout mAppBarLayout;
    private ImageView mToolbarImage;
    public static List<DataItem> mArticleList;

    private Toast mToast;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            startIntroAnimation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //todo : edit on switching API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DataFetcher.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DataFetcher fetcher = retrofit.create(DataFetcher.class);
        mAPICall = fetcher.getArticles();
        mAdCall = fetcher.getAds();

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        makeAPIRequest();


        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam);
        if (mFloatingActionMenu != null) {
            mFloatingActionMenu.getMenuIconView()
                    .setImageResource(R.drawable.ic_settings_fab);
            mFloatingActionMenu.setClosedOnTouchOutside(true);
        }


        FloatingActionButton mShareStoryFab = (FloatingActionButton) findViewById(R.id.fab_share_story);
        FloatingActionButton mSavedFab = (FloatingActionButton) findViewById(R.id.fab_saved);
        ImageView mSearchItem = (ImageView) findViewById(R.id.search);

        mSavedFab.setColorNormal(ContextCompat.getColor(this, R.color.fab_dark));
        mSavedFab.setColorPressed(ContextCompat.getColor(this, R.color.fab_dark));
        mShareStoryFab.setColorNormal(ContextCompat.getColor(this, R.color.fab_light));
        mShareStoryFab.setColorPressed(ContextCompat.getColor(this, R.color.fab_light));


        mSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsActivity.this, SearchActivity.class));
            }
        });

        mSavedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionMenu.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(NewsActivity.this, SavedArticlesActivity.class));
                    }
                }, INTENT_DELAY);

            }
        });

        mShareStoryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionMenu.close(true);
                if (mSnackBar.isShown()) return;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(NewsActivity.this, ShareActivity.class));
                    }
                }, INTENT_DELAY);
            }
        });

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

        mFloatingActionMenu.setIconToggleAnimatorSet(set);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarImage = (ImageView) findViewById(R.id.app_logo);

        if (mToolbarImage != null) {
            Glide.with(this).load(R.drawable.newslogo).into(mToolbarImage);
        }
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mArticleList = new ArrayList<DataItem>();
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mArticleList);

        // Set up the ViewPager with the sections adapter.
        if (mViewPager != null) {
            mViewPager.setAdapter(mMainPagerAdapter);
            mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.spacing_normal));
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);

            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

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
            });
        }
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
                        mFloatingActionMenu.animate()
                                .translationY(0)
                                .setInterpolator(new OvershootInterpolator(1.f))
                                .setStartDelay(300)
                                .setDuration(ANIM_DURATION_FAB)
                                .start();
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

    /**
     * Dismisses the stale toast before making a fresh one
     *
     * @param text         (String) toast text
     * @param longDuration (boolean) toast duration
     */
    public void showToast(String text, boolean longDuration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(NewsActivity.this, text,
                longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
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
        } else if (id == R.id.action_feedback) {
            Intent feedback = new Intent(Intent.ACTION_SEND)
                    .setType("text/email")
                    .putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.feedback_email)})
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
            startActivity(Intent.createChooser(feedback, getString(R.string.send_feedback)));
        } else if (id == R.id.action_login) {
            startActivity(new Intent(NewsActivity.this, SettingsActivity.class));
        } else if (id == R.id.action_rate) {
            //intent will start working after app is published
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("market://details?id=" + getPackageName())));
        } else if (id == R.id.action_notify) { // todo : remove after demo
            NotificationUtils.notifyUser(NewsActivity.this, (Story) mArticleList.get(0), 0);
            if (fetchCurrentFragment().newsCardAdapter != null) {
                fetchCurrentFragment().addItem((Story) mArticleList.get(0), 1);
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
        mSnackBar = Snackbar.make(
                mFloatingActionMenu,
                "No Connection",
                Snackbar.LENGTH_INDEFINITE).
                setAction(
                        "Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                makeAPIRequest();
                            }
                        })
                .setActionTextColor(ContextCompat.getColor(NewsActivity.this, R.color.fab_light));
        mSnackBar.show();
    }

    private void makeAPIRequest() {
        mProgress.setVisibility(View.VISIBLE);
        if (mAPICall.isExecuted()) {
            mAPICall.clone().enqueue(myCallback);
        } else {
            mAPICall.enqueue(myCallback);
        }
    }

    private void makeAdRequest() {
        if(mAdCall.isExecuted()) {
            mAdCall.clone().enqueue(myAdCallback);
        } else {
            mAdCall.enqueue(myAdCallback);
        }
    }

    private Callback<List<Advert>> myAdCallback = new Callback<List<Advert>>() {

        @Override
        public void onResponse(Call<List<Advert>> call, Response<List<Advert>> response) {
            if(response.isSuccessful()) {
                //todo: fix after demo
                mArticleList.add(3, response.body().get(0));
                mArticleList.add(6, response.body().get(2));
                fetchCurrentFragment().newsCardAdapter.mLoadList.addAll(mArticleList);
                fetchCurrentFragment().newsCardAdapter
                        .notifyItemRangeInserted(0, mArticleList.size());
                notifyPreLoadedFragment();
            } else {
                showDeviceOffline();
            }
        }

        @Override
        public void onFailure(Call<List<Advert>> call, Throwable t) {
            showDeviceOffline();
        }
    };

    private Callback<List<Story>> myCallback = new Callback<List<Story>>() {
        @Override
        public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
            if (response.isSuccessful()) {
                mArticleList.addAll(response.body());
                makeAdRequest();

            } else {
                showDeviceOffline();
            }
            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Call<List<Story>> call, Throwable t) {
            t.printStackTrace();
            showDeviceOffline();
            mProgress.setVisibility(View.GONE);
        }
    };
}
