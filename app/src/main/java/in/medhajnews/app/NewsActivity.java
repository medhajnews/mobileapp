package in.medhajnews.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import in.medhajnews.app.Adapter.MainPagerAdapter;

public class NewsActivity extends AppCompatActivity {

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

    private FloatingActionButton mMainFab;
    private FloatingActionButton miniFab1, miniFab2, miniFab3;
    private CardView cardsavedarticles, cardsettings, cardsharestory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mMainFab = (FloatingActionButton) findViewById(R.id.news_fab);
        miniFab1 = (FloatingActionButton) findViewById(R.id.view);
        miniFab2 = (FloatingActionButton) findViewById(R.id.view2);
        final FrameLayout relativeLayout = (FrameLayout) findViewById(R.id.overlay);

        cardsavedarticles = (CardView) findViewById(R.id.view7);
        cardsettings = (CardView) findViewById(R.id.view8);
        cardsharestory = (CardView) findViewById(R.id.view9);

        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shown) {
                    shown = false;
                    miniFab1.setVisibility(View.GONE);
                    miniFab2.setVisibility(View.GONE);
                    cardsharestory.setVisibility(View.GONE);
                    cardsettings.setVisibility(View.GONE);
                    cardsavedarticles.setVisibility(View.GONE);
                    relativeLayout.setBackgroundColor(ContextCompat.getColor(NewsActivity.this, android.R.color.transparent));
                } else {
                    shown = true;
                    miniFab1.setVisibility(View.VISIBLE);
                    miniFab2.setVisibility(View.VISIBLE);
                    cardsettings.setVisibility(View.VISIBLE);
                    cardsharestory.setVisibility(View.VISIBLE);
                    cardsavedarticles.setVisibility(View.VISIBLE);
                    relativeLayout.setBackgroundColor(ContextCompat.getColor(NewsActivity.this, R.color.dark_overlay));
                }

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView toolbarImage = (ImageView) findViewById(R.id.app_logo);

        if (toolbarImage != null) {
            Glide.with(this).load(R.drawable.newslogo).into(toolbarImage);
        }
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
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
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent search = new Intent(NewsActivity.this, SearchActivity.class);
            startActivity(search);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
