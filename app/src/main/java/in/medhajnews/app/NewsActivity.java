package in.medhajnews.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import in.medhajnews.app.Adapter.MainPagerAdapter;

public class NewsActivity extends AppCompatActivity {

    private final static int ANIM_DURATION_TOOLBAR = 300;
    private final static int ANIM_DURATION_FAB = 400;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     *
     * {@link FragmentPagerAdapter} (which will keep every
     * loaded fragment in memory)  becomes too memory intensive.
     */

    private MainPagerAdapter mMainPagerAdapter;
    boolean shown = false;
    public static TabLayout mTabLayout;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mSettingsFab, mShareStoryFab, mSavedFab;
    private AppBarLayout mAppBarLayout;
    private ImageView mToolbarImage;
    private ImageView mSearchItem;

    private Toast mToast;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(savedInstanceState==null) {
            startIntroAnimation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news);

        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam);
        if (mFloatingActionMenu != null) {
            mFloatingActionMenu.getMenuIconView()
                    .setImageResource(R.drawable.ic_settings_fab);
            mFloatingActionMenu.setClosedOnTouchOutside(true);
        }

        mSettingsFab = (FloatingActionButton) findViewById(R.id.fab_settings);
        mShareStoryFab = (FloatingActionButton) findViewById(R.id.fab_share_story);
        mSavedFab = (FloatingActionButton) findViewById(R.id.fab_saved);
        mSearchItem = (ImageView) findViewById(R.id.search);

        mSettingsFab.setColorNormal(ContextCompat.getColor(this, R.color.fab_dark));
        mSettingsFab.setColorPressed(ContextCompat.getColor(this, R.color.fab_dark));
        mSavedFab.setColorNormal(ContextCompat.getColor(this, R.color.fab_dark));
        mSavedFab.setColorPressed(ContextCompat.getColor(this, R.color.fab_dark));

        mSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(NewsActivity.this, SearchActivity.class);
                startActivity(search);
            }
        });

        mSavedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo : add saved articles activity
                showToast("Saved Articles", false);
            }
        });

        mSettingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionMenu.close(true);
                Intent settings  = new Intent(NewsActivity.this, SettingsActivity.class);
                startActivity(settings);
            }
        });

        mShareStoryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo : add share your story activity
//                showToast("Share your Story", false);
                mFloatingActionMenu.close(true);
                Intent camera = new Intent(NewsActivity.this, CameraActivity.class);
                startActivity(camera);
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
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        if (mViewPager != null) {
            mViewPager.setAdapter(mMainPagerAdapter);
            mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.spacing_normal));
        }

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);

            mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                    mAppBarLayout.setExpanded(true, true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_news, menu);
//
//        mSearchItem = (MenuItem) menu.findItem(R.id.action_search);
////        if(pendingAnimation) {
////            pendingAnimation=false;
////            startIntroAnimation();
////        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            Intent search = new Intent(NewsActivity.this, SearchActivity.class);
//            startActivity(search);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void startIntroAnimation(){
        mFloatingActionMenu.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.fab_size_normal));

        mAppBarLayout.setTranslationY(-100);
        mToolbarImage.setTranslationY(-100);
//        getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        mAppBarLayout.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        mToolbarImage.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400)
//        mSearchItem.getActionView().animate()
//                .translationY(0)
//                .setDuration(ANIM_DURATION_TOOLBAR)
//                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation(){
        mFloatingActionMenu.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
//        todo : fix code to animate recycler in fragment
//        MainFragment.newsCardAdapter.updateItems(true);
    }

    @Override
    public void onBackPressed() {
        if(mFloatingActionMenu.isOpened()) {
            mFloatingActionMenu.close(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        //don't rely on this to close menu, it doesn't look pretty
        //close floating action menu before leaving activity
        if(mFloatingActionMenu.isOpened()) {
            mFloatingActionMenu.close(true);
        }
        super.onPause();
    }

    /**
     * Dismisses the stale toast before making a fresh one
     * @param text (String) toast text
     * @param longDuration (boolean) toast duration
     */
    public void showToast( String text, boolean longDuration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(NewsActivity.this, text,
                longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }
}
