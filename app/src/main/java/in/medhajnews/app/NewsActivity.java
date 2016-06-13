package in.medhajnews.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mMainFab = (FloatingActionButton) findViewById(R.id.news_fab);

        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialog = getLayoutInflater().inflate(R.layout.news_dialog,
                        (ViewGroup) findViewById(R.id.base));
                new AlertDialog.Builder(NewsActivity.this)
                        .setCancelable(true)
                        .setView(dialog)
                        .show();

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_news, menu);
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
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
