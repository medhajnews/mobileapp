package in.medhajnews.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import in.medhajnews.app.Objects.Article;

public class ArticleActivity extends AppCompatActivity {

//    private TabLayout mBottomBar;
    private CardView mBottomBar;
    private ImageView mFontSizeIcon, mUISwitchIcon, mBookmarkIcon, mShareIcon;
    private boolean isUIdark = false;
    private boolean isUIfull = false;
    private boolean isArticleSaved = false;
    private LinearLayout mContentHolder;
    private ColorStateList oldtextColors;
//    private Toolbar mToolbar;
    private ImageView mArticleImageView;
    private Toast mLastToast;
    private SharedPreferences prefs;
    private ImageButton back;
    /**
     * Preferences                  DefaultValue
     * //font_size                      0
     * //is_ui_state_dark              false
     */

    private final static String TAG = ArticleActivity.class.getSimpleName();

    private TextView mTitleTextView, mContentTextView, mAuthorTextView, mDateTextView,
            mAreaTextView, mUpdateTextView;

    private Article mArticle;
    private AppBarLayout mAppBarView;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        Article article;
        if(getIntent()!=null) {
            article = getIntent().getParcelableExtra("Article");
        } else {
            article = Article.sampleArticle(this);
        }

        back = (ImageButton) findViewById(R.id.back);

        setContentView(R.layout.activity_article);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBottomBar = (CardView) findViewById(R.id.bottom_bar);
//        setSupportActionBar(mToolbar);
        mArticle = Article.sampleArticle(this);
//        mAppBarView = (AppBarLayout) findViewById(R.id.appbar_view);

//        if (getSupportActionBar() != null && article!=null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setTitle(article.Category);
//        }


        mFontSizeIcon = (ImageView) findViewById(R.id.font);
        mUISwitchIcon = (ImageView) findViewById(R.id.color);
        mBookmarkIcon = (ImageView) findViewById(R.id.bookmark);
        mShareIcon = (ImageView) findViewById(R.id.share);

        if(Boolean.parseBoolean(article.isArticleSaved)) {
            mBookmarkIcon.setImageResource(R.drawable.ic_saved_offline);
        }

        mFontSizeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change text size
                showTextSizeSelectionDialog();
            }
        });
        mUISwitchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle dark UI
                toggleNightMode(ArticleActivity.this);
            }
        });
        mBookmarkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download article to local database
                downloadArticle();
            }
        });
        mShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start share
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareBody = "Medhaj News"; // replace with article link
                String shareSubject = "Shared via Meddhaj News App"; //share subject
                share.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(share, "Share via"));
            }
        });

        prefs = getSharedPreferences("MedhajAppPreferences", Context.MODE_PRIVATE);
//        Typeface lato = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");

        mContentHolder = (LinearLayout) findViewById(R.id.content_holder);
        mArticleImageView = (ImageView) findViewById(R.id.main_image);
        mContentTextView = (TextView) findViewById(R.id.main_content);
        mTitleTextView = (TextView) findViewById(R.id.content_title);
        mAuthorTextView = (TextView) findViewById(R.id.content_author);
        mAreaTextView = (TextView) findViewById(R.id.content_area);
        mUpdateTextView = (TextView) findViewById(R.id.content_update_time);
        mDateTextView = (TextView) findViewById(R.id.content_date);
//        mContentTextView.setTypeface(lato);
        if (mArticleImageView != null) {
            mArticleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery = new Intent(ArticleActivity.this, GalleryActivity.class);
                    startActivity(gallery);
                }
            });
        }
        mContentTextView.setTextSize(prefs.getInt("font_size", 16));
        mContentTextView.setText(article.ArticleContent);
        mTitleTextView.setTextSize(prefs.getInt("font_size", 16) + 10);
        mTitleTextView.setText(article.ArticleTitle);
        mAuthorTextView.setText(article.ArticleAuthor);
        mAreaTextView.setText(article.ArticleArea);
        mDateTextView.setText(article.ArticleDate);
        mUpdateTextView.setText(article.ArticleUpdateTime);
        /*
            initialise oldtextcolors before calling nightmodetoggle
         */
        oldtextColors = mAuthorTextView.getTextColors();
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.main_scrollView);
        mContentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUIfull) {
                    toggleUIElements(ArticleActivity.this, true);
                } else {
                    toggleUIElements(ArticleActivity.this, false);
                }
            }
        });
        if(scrollView!=null) {
            scrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                }

                @Override
                public void onDownMotionEvent() {
                }

                @Override
                public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                    //UP is down and DOWN is up
                    if (scrollState == ScrollState.UP) {
                        toggleUIElements(ArticleActivity.this, false);
                    } else {
                        toggleUIElements(ArticleActivity.this, true);
                    }
                }

            });
        }
        Glide.with(this).load(article.ArticleImageLink).listener(mainImageLoadListener)
                .fitCenter().crossFade().into(mArticleImageView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (prefs.getBoolean("is_ui_state_dark", false)) {
            toggleNightMode(this);
        }
    }

    private void toggleUIElements(Context context, boolean showElements) {
        if (showElements && isUIfull) {
//            getSupportActionBar().show();
            isUIfull = false;
            mBottomBar.setVisibility(View.VISIBLE);
        } else if (!showElements && !isUIfull) {
//            getSupportActionBar().hide();
            isUIfull = true;
            mBottomBar.setVisibility(View.GONE);
        }
    }

    private void toggleNightMode(Context context) {
        //since the number of elements is small, every element will be separately toggled
        if (!isUIdark) {
            //make it dark
            isUIdark = true;
            mContentTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mContentTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAuthorTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mAuthorTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
//            mTitleTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
//            mTitleTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAreaTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mAreaTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mUpdateTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mUpdateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mDateTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mDateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mContentHolder.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            //toolbar
//            mToolbar.setNavigationIcon(R.drawable.ic_back_dark);
//            mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.white));
            mAppBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.actionBarDark));
//            getSupportActionBar().setBackgroundDrawable(
//                    new ColorDrawable(ContextCompat.getColor(context, R.color.actionBarDark)));
            //Bottombar
            mBottomBar.setBackgroundColor(ContextCompat.getColor(context, R.color.bottomBarDark));
            switchBottombarIcons(true, context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.statusBarDark));
            }
        } else {
            //make it light
            isUIdark = false;
            mContentTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mContentTextView.setTextColor(ContextCompat.getColor(context, R.color.article_content));
            mAuthorTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mAuthorTextView.setTextColor(oldtextColors);
//            mTitleTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
//            mTitleTextView.setTextColor(ContextCompat.getColor(context, R.color.article_title));
            mAreaTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mAreaTextView.setTextColor(oldtextColors);
            mUpdateTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mUpdateTextView.setTextColor(oldtextColors);
            mDateTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mDateTextView.setTextColor(oldtextColors);
            mContentHolder.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            //toolbar
//            mToolbar.setNavigationIcon(R.drawable.ic_back_light);
//            mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.darkui));
            mAppBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//            getSupportActionBar().setBackgroundDrawable(
//                    new ColorDrawable(ContextCompat.getColor(context, R.color.white)));
            switchBottombarIcons(false, context);
            //bottombar
            mBottomBar.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }

        }
        Log.d(TAG, "toggleNightMode: " + String.valueOf(isUIdark));
    }

    private void downloadArticle() {
        Log.d(TAG, "downloadArticle:");
        //download into local database
        if (!isArticleSaved) {
            showToast(getString(R.string.download_toast), false);
            isArticleSaved = true;
            mBookmarkIcon.setImageResource(R.drawable.ic_saved_offline);
        } else {
            showToast(getString(R.string.article_delete), false);
            isArticleSaved = false; //let a method handle this variable and edit the database
            mBookmarkIcon.setImageResource(R.drawable.ic_save_offline);
        }
        if(isUIdark) {
            applyThemeToDrawable(this, mBookmarkIcon.getDrawable());
        }
    }

    private void showTextSizeSelectionDialog() {
        Log.d(TAG, "showTextSizeSelectionDialog: ");
        final Dialog fontSelectionDialog = new Dialog(ArticleActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.font_picker_dialog,
                (ViewGroup) findViewById(R.id.base));
        fontSelectionDialog.setContentView(layout);

        final DiscreteSeekBar seekBar = (DiscreteSeekBar) layout.findViewById(R.id.font_seekbar);
        ImageView imageView = (ImageView) layout.findViewById(R.id.fontsizesmall);
        if(isUIdark) {
            applyThemeToDrawable(this, imageView.getDrawable());
            ((LinearLayout) layout.findViewById(R.id.base)).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.fontDialogDark)
            );
        }
        seekBar.setProgress(prefs.getInt("font_size_multiplier", 0));
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    /*
                    view         initial sizes    maximum sizes
                    content        16sp               33sp
                    title          24sp               41sp
                     */
                    mContentTextView.setTextSize((progress));
                    mTitleTextView.setTextSize((progress + 10));
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                //navigate up from same task does not recreate the state
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showToast(String text, boolean longDuration) {
        if (mLastToast != null) mLastToast.cancel();
        mLastToast = Toast.makeText(ArticleActivity.this, text,
                longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mLastToast.setGravity(Gravity.CENTER, 0, 0);
        mLastToast.show();
    }

    public void switchBottombarIcons(boolean makelight, Context context) {
        if(makelight) {
            applyThemeToDrawable(context, mFontSizeIcon.getDrawable());
            applyThemeToDrawable(context, mUISwitchIcon.getDrawable());
            applyThemeToDrawable(context, mBookmarkIcon.getDrawable());
            applyThemeToDrawable(context, mShareIcon.getDrawable());
        } else {
            mFontSizeIcon.setImageResource(R.drawable.ic_text_size);
            mUISwitchIcon.setImageResource(R.drawable.ic_night_mode_toggle);
            if(!isArticleSaved) {
                mBookmarkIcon.setImageResource(R.drawable.ic_save_offline);
            } else {
                mBookmarkIcon.setImageResource(R.drawable.ic_saved_offline);
            }
            mShareIcon.setImageResource(R.drawable.ic_share);
        }
    }

    public void applyThemeToDrawable(Context context, Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.lightIcon),
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }

    private RequestListener mainImageLoadListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model,
                                       Target<GlideDrawable> target, boolean isFromMemoryCache,
                                       boolean isFirstResource) {
            final Bitmap bitmap = GlideUtils.getBitmap(resource);
            final int twentyFourDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, ArticleActivity.this.getResources().getDisplayMetrics());
            Palette.from(bitmap)
                    .maximumColorCount(3)
                    .clearFilters() /* by default palette ignore certain hues
                        (e.g. pure black/white) but we don't want this. */
                    .setRegion(0, 0, bitmap.getWidth() - 1, twentyFourDip) /* - 1 to work around
                        https://code.google.com/p/android/issues/detail?id=191013 */
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            boolean isDark;
                            @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                            if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                            } else {
                                isDark = lightness == ColorUtils.IS_DARK;
                            }

                            if (!isDark) { // make back icon dark on light images
                                back.setColorFilter(ContextCompat.getColor(
                                        ArticleActivity.this, R.color.dark_icon));
                            }
                        }
                    });
            return false;
        }
    };
}
