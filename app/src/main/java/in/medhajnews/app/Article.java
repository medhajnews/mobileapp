package in.medhajnews.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import in.medhajnews.app.Widgets.FontSelectionSeekBar;

public class Article extends AppCompatActivity {

    private TabLayout mBottomBar;
    private boolean isUIdark = false;
    private boolean isUIfull = false;
    private LinearLayout mContentHolder;
    private ColorStateList oldtextColors;

    private final static String TAG = Article.class.getSimpleName();

    private TextView mTitleTextView, mContentTextView, mAuthorTextView, mDateTextView,
            mAreaTextView, mUpdateTextView;

    private boolean isOrientationLandscape = false;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View view = getLayoutInflater().inflate(R.layout.medhaj_tab, null);
            mBottomBar.removeAllTabs();
            mBottomBar.addTab(mBottomBar.newTab().setCustomView(view));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_text_size));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_night_mode_toggle));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_save_offline));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_share));
            isOrientationLandscape = true;
        } else {
            mBottomBar.removeAllTabs();
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_menu_camera));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_text_size));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_night_mode_toggle));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_save_offline));
            mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_share));
            isOrientationLandscape = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Typeface lato =Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");

        mBottomBar = (TabLayout) findViewById(R.id.bottom_bar);
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_menu_camera));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_text_size));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_night_mode_toggle));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_save_offline));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_share));
        mBottomBar.setTabMode(TabLayout.MODE_FIXED);
        mBottomBar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition()==1) {
                    //change text size
                    showTextSizeSelectionDialog();
                } else if(tab.getPosition() == 2) {
                    //toggle dark UI
                    toggleNightMode();
                } else if(tab.getPosition() == 3) {
                    //download article to local database
                    downloadArticle();
                } else if(tab.getPosition() == 4) {
                    //start share
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    String shareBody = "Medhaj News"; // replace with article link
                    String shareSubject = "Shared via Meddhaj News App"; //share subject
                    share.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                    share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(share, "Share via"));
                }
                /*
                since we used tabs instead of buttons, we need to reset them so that the same selection
                can be made twice. tab(0) is the medhaj logo.
                 */
                mBottomBar.getTabAt(0).select();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });

        mContentHolder = (LinearLayout) findViewById(R.id.content_holder);
        ImageView articleImageView = (ImageView) findViewById(R.id.main_image);
        mContentTextView = (TextView) findViewById(R.id.main_content);
        mTitleTextView = (TextView) findViewById(R.id.content_title);
        mAuthorTextView = (TextView) findViewById(R.id.content_author);
        mAreaTextView = (TextView) findViewById(R.id.content_area);
        mUpdateTextView = (TextView) findViewById(R.id.content_update_time);
        mDateTextView = (TextView) findViewById(R.id.content_date);
        mContentTextView.setTypeface(lato);
        oldtextColors = mContentTextView.getTextColors();

        Glide.with(this).load(R.drawable.code).centerCrop().crossFade().into(articleImageView);
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.main_scrollView);

        scrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                //don't while with dragging, intense lag
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                //show actionbar
//                if(scrollState == ScrollState.UP) {
//                    mBottomBar.setVisibility(View.VISIBLE);
//                    getSupportActionBar().show();
//                } else if (scrollState == ScrollState.DOWN) {
//                    getSupportActionBar().hide();
//                    mBottomBar.setVisibility(View.GONE);
//                }
            }

        });



    }

    private void toggleUIElements(Context context, boolean showElements) {
        if(showElements && isUIfull && getSupportActionBar() != null) {
            getSupportActionBar().show();
            mBottomBar.setVisibility(View.VISIBLE);
        } else if (!showElements && !isUIfull && getSupportActionBar() != null) {
            getSupportActionBar().hide();
            mBottomBar.removeAllTabs();
        }
    }

    private void toggleNightMode() {
        //since the number of elements is small, every element will be separately toggled
        if(!isUIdark) {
            //make it dark
            isUIdark = true;
            mContentTextView.setBackgroundColor(getResources().getColor(R.color.darkui));
            mContentTextView.setTextColor(getResources().getColor(android.R.color.white));
            mAuthorTextView.setBackgroundColor(getResources().getColor(R.color.darkui));
            mAuthorTextView.setTextColor(getResources().getColor(android.R.color.white));
            mTitleTextView.setBackgroundColor(getResources().getColor(R.color.darkui));
            mTitleTextView.setTextColor(getResources().getColor(android.R.color.white));
            mAreaTextView.setBackgroundColor(getResources().getColor(R.color.darkui));
            mAreaTextView.setTextColor(getResources().getColor(android.R.color.white));
            mUpdateTextView.setBackgroundColor(getResources().getColor(R.color.darkui));
            mUpdateTextView.setTextColor(getResources().getColor(android.R.color.white));
            mDateTextView.setBackgroundColor(getResources().getColor(R.color.darkui));
            mDateTextView.setTextColor(getResources().getColor(android.R.color.white));
            mContentHolder.setBackgroundColor(getResources().getColor(R.color.darkui));
        } else {
            //make it light
            isUIdark = false;
            mContentTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
            mContentTextView.setTextColor(oldtextColors);
            mAuthorTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
            mAuthorTextView.setTextColor(oldtextColors);
            mTitleTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
            mTitleTextView.setTextColor(oldtextColors);
            mAreaTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
            mAreaTextView.setTextColor(oldtextColors);
            mUpdateTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
            mUpdateTextView.setTextColor(oldtextColors);
            mDateTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
            mDateTextView.setTextColor(oldtextColors);
            mContentHolder.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void downloadArticle() {
        Log.d(TAG, "downloadArticle:");
        //download into local database
        Toast.makeText(Article.this, R.string.download_toast, Toast.LENGTH_SHORT).show();
    }

    private void showTextSizeSelectionDialog() {
        Log.d(TAG, "showTextSizeSelectionDialog: ");
        final Dialog fontSelectionDialog = new Dialog(Article.this);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.font_picker_dialog,
                (ViewGroup)findViewById(R.id.base));
        fontSelectionDialog.setContentView(layout);

        Button okayButton = (Button) layout.findViewById(R.id.okay_button);
        FontSelectionSeekBar seekBar = (FontSelectionSeekBar) layout.findViewById(R.id.font_seekbar);
        seekBar.setMax(33);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fontSelectionDialog.isShowing()) {
                    fontSelectionDialog.dismiss();
                }
            }
        });
        fontSelectionDialog.setCancelable(true);
        fontSelectionDialog.setTitle("Select Font Size");
        fontSelectionDialog.show();
    }
}
