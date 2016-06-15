package in.medhajnews.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import in.medhajnews.app.Utils.ColorUtils;
import in.medhajnews.app.Utils.GlideUtils;

public class ArticleActivity extends AppCompatActivity {


    private CardView mBottomBar;
    private ImageView mFontSizeIcon, mUISwitchIcon, mBookmarkIcon, mShareIcon;
    private boolean isUIdark = false;
    private boolean isUIfull = false;
    private boolean isArticleSaved = false;
    private LinearLayout mContentHolder;
    private ColorStateList oldtextColors;

    private ImageView mArticleImageView;
    private Toast mToast;
    private SharedPreferences prefs;
    private ImageButton mBackArrow, mSearchIcon;
    /**
     * Preferences                  DefaultValue
     * //font_size                      16
     * //is_ui_state_dark              false
     */

    //todo : show hints on first run

    private final static String TAG = ArticleActivity.class.getSimpleName();

    private TextView mTitleTextView, mContentTextView, mAuthorTextView, mDateTextView,
            mAreaTextView, mUpdateTextView;

    private Article mArticle;


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

        if(getIntent()!=null) {
            mArticle = getIntent().getParcelableExtra("Article");
        } else {
            finish();
        }


        setContentView(R.layout.activity_article);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);
        mSearchIcon = (ImageButton) findViewById(R.id.search_icon);
        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(ArticleActivity.this, SearchActivity.class);
                startActivity(search);
            }
        });
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBottomBar = (CardView) findViewById(R.id.bottom_bar);
        mArticle = Article.sampleArticle(this);

        mFontSizeIcon = (ImageView) findViewById(R.id.font);
        mUISwitchIcon = (ImageView) findViewById(R.id.color);
        mBookmarkIcon = (ImageView) findViewById(R.id.bookmark);
        mShareIcon = (ImageView) findViewById(R.id.share);
        /**
         * Share dialog takes a little time to load, On Android 5+ we'll animate the share icon to
         * hide the delay. Unfortunately no feasible method of animations exist for lower API's
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mShareIcon !=null) {
            mShareIcon.setImageResource(R.drawable.avd_share);
        }

        if(Boolean.parseBoolean(mArticle.isArticleSaved)) {
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
                downloadArticle(ArticleActivity.this);
            }
        });
        mShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo (LP): check if share compilation is faster in production builds
                //animate icon if API > 21
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mShareIcon !=null) {
                    ((AnimatedVectorDrawable) mShareIcon.getDrawable()).start();
                }
                //start share
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareBody = mArticle.ArticleTitle + " " + mArticle.ArticleLink;// replace with article link
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
        mContentTextView.setText(mArticle.ArticleContent);
        mTitleTextView.setText(mArticle.ArticleTitle);
        mAuthorTextView.setText(mArticle.ArticleAuthor);
        mAreaTextView.setText(mArticle.ArticleArea);
        mDateTextView.setText(mArticle.ArticleDate);
        mUpdateTextView.setText(mArticle.ArticleUpdateTime);
        /*
            initialise oldtextcolors before calling nightmodetoggle
         */
        oldtextColors = mAuthorTextView.getTextColors();
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.main_scrollView);
        mContentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUIElements();
            }
        });
        mContentTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ArticleActivity.this).setTitle("More Stuff")
                        .setMessage("Placeholder").show();
                return true;
            }
        });
        //todo : replace observable scrollview with observable recyclerview
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
                        toggleUIElements();
                    } else {
                        toggleUIElements();
                    }
                }

            });
        }

        Glide.with(this).load(mArticle.ArticleImageLink).listener(mainImageLoadListener)
                .fitCenter().crossFade().into(mArticleImageView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (prefs.getBoolean("is_ui_state_dark", false)) {
            toggleNightMode(this);
        }
    }

    private void toggleUIElements() {
        //todo : fix animation
        if (isUIfull) {
            isUIfull = false;
            mBottomBar.setVisibility(View.VISIBLE);
//            bottomBarAnimateIn();
        } else {
            isUIfull = true;
            mBottomBar.setVisibility(View.GONE);
//            bottomBarAnimateOut();
        }
    }

    private void toggleNightMode(Context context) {
        if (!isUIdark) {
            //make it dark
            isUIdark = true;
            mContentTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mContentTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAuthorTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mAuthorTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAreaTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mAreaTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mUpdateTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mUpdateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mDateTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mDateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mContentHolder.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
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
            mAreaTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mAreaTextView.setTextColor(oldtextColors);
            mUpdateTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mUpdateTextView.setTextColor(oldtextColors);
            mDateTextView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            mDateTextView.setTextColor(oldtextColors);
            mContentHolder.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            switchBottombarIcons(false, context);
            //bottombar
            mBottomBar.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }

        }
    }

    private void downloadArticle(Context context) {
        //todo : download into local database
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
            ColorUtils.applyThemeToDrawable(this, mBookmarkIcon.getDrawable(), R.color.lightIcon);
        }
    }

    private void showTextSizeSelectionDialog() {
        final Dialog fontSelectionDialog = new Dialog(ArticleActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.font_picker_dialog,
                (ViewGroup) findViewById(R.id.base));
        fontSelectionDialog.setContentView(layout);

        final DiscreteSeekBar seekBar = (DiscreteSeekBar) layout.findViewById(R.id.font_seekbar);
        ImageView imageView = (ImageView) layout.findViewById(R.id.fontsizesmall);
        if(isUIdark) {
            ColorUtils.applyThemeToDrawable(this, imageView.getDrawable(), R.color.lightIcon);
            layout.findViewById(R.id.base).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.fontDialogDark)
            );
        }
        seekBar.setProgress(prefs.getInt("font_size_multiplier", 0));
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    /**
                     *   view         initial sizes    maximum sizes
                     *   content        16sp               33sp
                     */
                    mContentTextView.setTextSize((progress));
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchBottombarIcons(boolean makelight, Context context) {
        if(makelight) {
            ColorUtils.applyThemeToDrawable(context, mFontSizeIcon.getDrawable(), R.color.lightIcon);
            ColorUtils.applyThemeToDrawable(context, mUISwitchIcon.getDrawable(), R.color.lightIcon);
            ColorUtils.applyThemeToDrawable(context, mBookmarkIcon.getDrawable(), R.color.lightIcon);
            ColorUtils.applyThemeToDrawable(context, mShareIcon.getDrawable(), R.color.lightIcon);
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



    private RequestListener mainImageLoadListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model,
                                       Target<GlideDrawable> target, boolean isFromMemoryCache,
                                       boolean isFirstResource) {
            final Bitmap bitmap = GlideUtils.getBitmap(resource);
            final int twentyFourDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, ArticleActivity.this.getResources().getDisplayMetrics());
            if (bitmap != null) {
                Palette.from(bitmap)
                        .maximumColorCount(3)
                        .clearFilters() /* by default palette ignores certain hues
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
                                    mBackArrow.setColorFilter(ContextCompat.getColor(
                                            ArticleActivity.this, R.color.dark_icon));
                                    mSearchIcon.setColorFilter(ContextCompat.getColor(
                                            ArticleActivity.this, R.color.dark_icon));
                                }
                            }
                        });
            }
            return false;
        }
    };

    private void bottomBarAnimateIn() {
        Animation slideUpAnimation = AnimationUtils.loadAnimation(ArticleActivity.this,
                R.anim.fab_slide_up);
        slideUpAnimation.setInterpolator(new FastOutSlowInInterpolator());
        slideUpAnimation.setDuration(200L);
        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomBar.startAnimation(slideUpAnimation);
    }

    private void bottomBarAnimateOut() {
        Animation slideDownAnimation = AnimationUtils.loadAnimation(ArticleActivity.this,
                R.anim.fab_slide_down);
        slideDownAnimation.setDuration(200L);
        slideDownAnimation.setInterpolator(new FastOutSlowInInterpolator());
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomBar.startAnimation(slideDownAnimation);
    }

    /**
     * Dismisses the stale toast before making a fresh one
     * @param text (String) toast text
     * @param longDuration (boolean) toast duration
     */
    public void showToast( String text, boolean longDuration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(ArticleActivity.this, text,
                longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }
}
