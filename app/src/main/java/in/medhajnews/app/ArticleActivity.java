package in.medhajnews.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class ArticleActivity extends AppCompatActivity {

    private TabLayout mBottomBar;
    private boolean isUIdark = false;
    private boolean isUIfull = false;
    private boolean isArticleSaved = false;
    private LinearLayout mContentHolder;
    private ColorStateList oldtextColors;

    private SharedPreferences prefs;
    /**
     *   Preferences                  DefaultValue
     *   //font_size                      0
     *   //is_ui_state_dark              false
     */

    private final static String TAG = ArticleActivity.class.getSimpleName();

    private TextView mTitleTextView, mContentTextView, mAuthorTextView, mDateTextView,
            mAreaTextView, mUpdateTextView;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_ui_state_dark", isUIdark);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Security"); //Article Category
        }

        prefs = getSharedPreferences("MedhajAppPreferences", Context.MODE_PRIVATE);
        Typeface lato =Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");

        mBottomBar = (TabLayout) findViewById(R.id.bottom_bar);
        setUpBottomBar();
        mBottomBar.setTabMode(TabLayout.MODE_FIXED);
        mBottomBar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition()==1) {
                    //change text size
                    showTextSizeSelectionDialog();
                }
                else if(tab.getPosition() == 2) {
                    //toggle dark UI
                    toggleNightMode(ArticleActivity.this);
                }
                else if(tab.getPosition() == 3) {
                    //download article to local database
                    downloadArticle();
                }
                else if(tab.getPosition() == 4) {
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
                can be made twice. tab(0) is the Medhaj logo.
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
        mContentTextView.setTextSize(prefs.getInt("font_size", 16));
        mTitleTextView.setTextSize(prefs.getInt("font_size", 16) + 8);
        /*
            initialise oldtextcolors before calling nightmodetoggle
         */
        oldtextColors = mContentTextView.getTextColors();
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.main_scrollView);
        mContentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUIfull) {
                    toggleUIElements(ArticleActivity.this, true);
                } else {
                    toggleUIElements(ArticleActivity.this, false);
                }
            }
        });
        assert scrollView != null;
        scrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            }

            @Override
            public void onDownMotionEvent() {
//                toggleUIElements(false);
            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//                toggleUIElements(true);
                //UP is down and DOWN is up
                if(scrollState == ScrollState.UP) {
                    toggleUIElements(ArticleActivity.this, false);
                } else {
                    toggleUIElements(ArticleActivity.this, true);
                }
            }

        });
        /*
        load image after everything to keep focus on top of view
         */
        if(articleImageView!=null) {
            Glide.with(this).load(R.drawable.code).centerCrop().crossFade().into(articleImageView);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(prefs.getBoolean("is_ui_state_dark", false)) {
            toggleNightMode(this);
        }
    }

    private void toggleUIElements(Context context, boolean showElements) {
        if(showElements && isUIfull && getSupportActionBar() != null) {
            getSupportActionBar().show();
            isUIfull = false;
            mBottomBar.setVisibility(View.VISIBLE);
        } else if (!showElements && !isUIfull && getSupportActionBar() != null) {
            getSupportActionBar().hide();
            isUIfull = true;
            mBottomBar.setVisibility(View.GONE);
        }
    }

    private void toggleNightMode(Context context) {
        //since the number of elements is small, every element will be separately toggled
        if(!isUIdark) {
            //make it dark
            isUIdark = true;
            mContentTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mContentTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAuthorTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mAuthorTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mTitleTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mTitleTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAreaTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mAreaTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mUpdateTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mUpdateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mDateTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mDateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mContentHolder.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
        } else {
            //make it light
            isUIdark = false;
            mContentTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mContentTextView.setTextColor(oldtextColors);
            mAuthorTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mAuthorTextView.setTextColor(oldtextColors);
            mTitleTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mTitleTextView.setTextColor(ContextCompat.getColor(context, R.color.article_title));
            mAreaTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mAreaTextView.setTextColor(oldtextColors);
            mUpdateTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mUpdateTextView.setTextColor(oldtextColors);
            mDateTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mDateTextView.setTextColor(oldtextColors);
            mContentHolder.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }
        Log.d(TAG, "toggleNightMode: " + String.valueOf(isUIdark));
    }

    private void downloadArticle() {
        Log.d(TAG, "downloadArticle:");
        //download into local database
        if (!isArticleSaved) {
            Toast.makeText(ArticleActivity.this, R.string.download_toast, Toast.LENGTH_SHORT).show();
            isArticleSaved = true;
            mBottomBar.getTabAt(3).setIcon(R.drawable.ic_saved_offline);
        }
        else {
            Toast.makeText(ArticleActivity.this, R.string.article_delete, Toast.LENGTH_SHORT).show();
            isArticleSaved = false; //let a method handle this variable and edit the database
            mBottomBar.getTabAt(3).setIcon(R.drawable.ic_save_offline);
        }
    }

    private void showTextSizeSelectionDialog() {
        Log.d(TAG, "showTextSizeSelectionDialog: ");
        final Dialog fontSelectionDialog = new Dialog(ArticleActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.font_picker_dialog,
                (ViewGroup)findViewById(R.id.base));
        fontSelectionDialog.setContentView(layout);

        final DiscreteSeekBar seekBar = (DiscreteSeekBar) layout.findViewById(R.id.font_seekbar);
        seekBar.setProgress(prefs.getInt("font_size_multiplier", 0));
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    /*
                    view         initial sizes    maximum sizes
                    content        16sp               33sp
                    title          24sp               41sp
                     */
                    mContentTextView.setTextSize((progress));
                    mTitleTextView.setTextSize((progress+8));
                }
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {}
        });
        fontSelectionDialog.setCancelable(true);
        fontSelectionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("font_size", seekBar.getProgress());
                editor.apply();
                dialog.dismiss();
            }
        });
        Window window = fontSelectionDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        seekBar.setProgress(prefs.getInt("font_size", 16));
        fontSelectionDialog.show();
    }

    private void setUpBottomBar() {
        mBottomBar.removeAllTabs();
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.medhaj_tab));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_text_size));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_night_mode_toggle));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_save_offline));
        mBottomBar.addTab(mBottomBar.newTab().setIcon(R.drawable.ic_share));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                /*
                    navigate up from same task does not recreate the state
                 */
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
