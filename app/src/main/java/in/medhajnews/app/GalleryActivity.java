package in.medhajnews.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import in.medhajnews.app.adapters.GalleryPagerAdapter;

public class GalleryActivity extends AppCompatActivity {

    private static String TAG = GalleryActivity.class.getSimpleName();

    /*
        Battery status is delivered via BroadcastReceiver by the Android System
     */
    private int level;
    private TextView batteryView;
    private BroadcastReceiver mBatteryBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryView.setText(String.valueOf(level) + "%");
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mBatteryBroadCastReceiver);
        } catch(IllegalArgumentException e) {
            Log.e(TAG, "onDestroy:" + e.getMessage());
        }
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private GalleryPagerAdapter mGalleryPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        TextView timeView = (TextView) findViewById((R.id.time));
        final TextView pageView = (TextView) findViewById(R.id.page);
        batteryView = (TextView) findViewById(R.id.battery);
        timeView.setText(DateFormat.getDateTimeInstance().format(new Date()));
        this.registerReceiver(this.mBatteryBroadCastReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        TextView imageDesc = (TextView) findViewById(R.id.image_description);
        imageDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GalleryActivity.this)
                        .setMessage("Hyper Text Markup Language is commonly used in Webpages")
                        .setCancelable(true)
                        .show();
            }
        });


        // Create the adapter that will return a fragment for each of the three
        // primary Gallery of the activity.
        mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the Gallery adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mGalleryPagerAdapter);
        pageView.setText("1/" + String.valueOf(mGalleryPagerAdapter.getCount()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                pageView.setText(String.valueOf(position + 1)+ "/" +
                        String.valueOf(mGalleryPagerAdapter.getCount()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
