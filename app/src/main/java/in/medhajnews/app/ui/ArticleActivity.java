package in.medhajnews.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import in.medhajnews.app.R;
import in.medhajnews.app.data.ArticleDBHelper;
import in.medhajnews.app.data.api.models.Story;
import in.medhajnews.app.ui.fragments.BottomSheet;
import in.medhajnews.app.ui.widget.FourThreeImageView;
import in.medhajnews.app.util.ColorUtils;
import in.medhajnews.app.util.GlideUtils;
import in.medhajnews.app.util.Utils;

public class ArticleActivity extends AppCompatActivity {

    private CardView mBottomBar;
    private ImageView mFontSizeIcon, mUISwitchIcon, mBookmarkIcon, mShareIcon;
    private boolean isUiDark = false;
    private LinearLayout mContentHolder;
    private ColorStateList OldTextColor;
    TextView mTitleTextView;
    private ArticleDBHelper mArticleDBHelper;
    private CardView mContentCard;

    private ImageView like, dislike, comment;

    private Toast mToast;
    private SharedPreferences prefs;
    private ImageButton mBackArrow, mSearchIcon;
    /**
     * Preferences                  DefaultValue
     * //font_size                      16
     * //is_ui_state_dark              false
     * //first_run                      true
     *
     * //todo : add ranking counter as described in {@link in.medhajnews.app.data.ArticleDBHelper}
     */

    //todo : show hints on first run
    //todo : add tablet compatibility

    private final static String TAG = ArticleActivity.class.getSimpleName();

    private TextView mContentTextView;
    private TextView mAuthorTextView;
    private TextView mDateTextView;
    private TextView mAreaTextView;
    private TextView mUpdateTextView;

    private Story mStory;

    private CoordinatorLayout baseLayout;


    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_ui_state_dark", isUiDark);
        editor.apply();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        mStory = getIntent().getParcelableExtra(Story.INTENT_EXTRA);
        prefs = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_article);

        mArticleDBHelper = new ArticleDBHelper(this);

        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);
        mSearchIcon = (ImageButton) findViewById(R.id.search_icon);
        baseLayout = (CoordinatorLayout) findViewById(R.id.article_base);
        mContentCard = (CardView) findViewById(R.id.article_card);
        final BottomSheetDialogFragment commentSheet = new BottomSheet();

        like = (ImageView) findViewById(R.id.like);
        dislike = (ImageView) findViewById(R.id.unlike);
        comment = (ImageView) findViewById(R.id.comment);

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentSheet.show(getSupportFragmentManager(), commentSheet.getTag());
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo like
                showToast(getString(R.string.action_like), false);
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo dislike
                showToast(getString(R.string.action_dislike), false);
            }
        });

        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArticleActivity.this, SearchActivity.class));
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBottomBar = (CardView) findViewById(R.id.bottom_bar);

        mFontSizeIcon = (ImageView) findViewById(R.id.font);
        mUISwitchIcon = (ImageView) findViewById(R.id.color);
        mBookmarkIcon = (ImageView) findViewById(R.id.bookmark);
        mShareIcon = (ImageView) findViewById(R.id.share);

        /**
         * Share dialog takes a little time to load, On Android 5+ we'll animate the share icon to
         * hide the delay.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mShareIcon != null) {
            mShareIcon.setImageResource(R.drawable.avd_share);
        }

        if (mStory.isSaved) {
            mBookmarkIcon.setImageResource(R.drawable.ic_saved_offline);
        }

        mFontSizeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextSizeSelectionDialog();
            }
        });
        mUISwitchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {toggleNightMode(ArticleActivity.this);
            }
        });
        mBookmarkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {downloadArticle(mStory);
            }
        });
        mShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo (LP): check if share compilation is faster in production builds
                //animate icon if API > 21
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mShareIcon != null) {
                    try {
                        ((AnimatedVectorDrawable) mShareIcon.getDrawable()).start();
                    } catch (ClassCastException err) {
                        Log.e(TAG, "Failed to animate icon: Share");
                    }
                }
                //start share
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareBody = mStory.title + "\n" + mStory.title;
                String shareSubject = mStory.title; //share subject
                share.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(share, "Share via"));
            }
        });


        mContentHolder = (LinearLayout) findViewById(R.id.content_holder);
        FourThreeImageView mArticleImageView = (FourThreeImageView) findViewById(R.id.main_image);
        mContentTextView = (TextView) findViewById(R.id.main_content);
        mTitleTextView = (TextView) findViewById(R.id.content_title);
        mAuthorTextView = (TextView) findViewById(R.id.content_author);
        mAreaTextView = (TextView) findViewById(R.id.content_area);
        mUpdateTextView = (TextView) findViewById(R.id.content_update_time);
        mDateTextView = (TextView) findViewById(R.id.content_date);
        Typeface cambo = Typeface.createFromAsset(getAssets(), "fonts/Cambo.otf");
        mContentTextView.setTypeface(cambo);

        mArticleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ArticleActivity.this, GalleryActivity.class));
            }
        });

        mContentTextView.setText(mStory.content.replace("<<>>", "\n\n"));
        mTitleTextView.setText(mStory.title.trim());
        mAuthorTextView.setText(mStory.author);
        mAreaTextView.setText(mStory.area);
        mDateTextView.setText(mStory.date);
        mUpdateTextView.setText(mStory.time);
        OldTextColor = mAuthorTextView.getTextColors();
        //todo : replace scrollview with recyclerview for more content type
        TextView category = (TextView) findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        category.setText(String.format(" %s ", mStory.category.toUpperCase()));
        Glide.with(this)
                .load(mStory.link_image.get(0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(mainImageLoadListener)
//                .fitCenter()
                .crossFade()
                .into(mArticleImageView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (prefs.getBoolean("is_ui_state_dark", false)) toggleNightMode(this);
        mContentTextView.setTextSize(prefs.getInt("font_size", 16));
        mTitleTextView.setTextSize(prefs.getInt("font_size", 16)+8);
    }

    private void toggleNightMode(Context context) {
        if (!isUiDark) {
            isUiDark = true;
            mContentTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAuthorTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mAreaTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mUpdateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mTitleTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            mDateTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            baseLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            mBottomBar.setBackgroundColor(ContextCompat.getColor(context, R.color.bottomBarDark));
            mContentCard.setBackgroundColor(ContextCompat.getColor(context, R.color.darkui));
            switchBottomBarIcons(true, context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.statusBarDark));
            }
        } else {
            isUiDark = false;
            mContentTextView.setTextColor(ContextCompat.getColor(context, R.color.article_content));
            mAuthorTextView.setTextColor(OldTextColor);
            mAreaTextView.setTextColor(OldTextColor);
            mUpdateTextView.setTextColor(OldTextColor);
            mDateTextView.setTextColor(OldTextColor);
            mTitleTextView.setTextColor(ContextCompat.getColor(context, R.color.article_title));
            baseLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            mContentCard.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            switchBottomBarIcons(false, context);
            mBottomBar.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }
        }
    }

    private void downloadArticle(Story story) {
        if (!story.isSaved) {
            showToast(getString(R.string.download_toast), false);
            mArticleDBHelper.SaveArticle(story);
            mBookmarkIcon.setImageResource(R.drawable.ic_saved_offline);
        } else {
            showToast(getString(R.string.article_delete), false);
            mArticleDBHelper.DeleteArticle(story);
            mBookmarkIcon.setImageResource(R.drawable.ic_save_offline);
        }
        if (isUiDark) {
            ColorUtils.applyThemeToDrawable(this, mBookmarkIcon.getDrawable(), R.color.lightIcon);
        }
    }

    private void showTextSizeSelectionDialog() {
        final Dialog fontSelectionDialog = new Dialog(ArticleActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.font_picker_dialog,
                (ViewGroup) findViewById(R.id.base));
        fontSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fontSelectionDialog.setContentView(layout);

        final DiscreteSeekBar seekBar = (DiscreteSeekBar) layout.findViewById(R.id.font_seekbar);
        ImageView imageView = (ImageView) layout.findViewById(R.id.font_icon);
        if (isUiDark) {
            ColorUtils.applyThemeToDrawable(this, imageView.getDrawable(), R.color.lightIcon);
            layout.findViewById(R.id.base).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.fontDialogDark)
            );
        }
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    /**
                     *   view         initial sizes    maximum sizes
                     *   content        16sp               32sp
                     *   title          24sp               40sp
                     */
                    mContentTextView.setTextSize(progress);
                    mTitleTextView.setTextSize(progress + 8);
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

    public void switchBottomBarIcons(boolean lightMode, Context context) {
        int color = lightMode ? R.color.lightIcon : R.color.mid_grey;
        ColorUtils.applyThemeToDrawable(context, mFontSizeIcon.getDrawable(), color);
        ColorUtils.applyThemeToDrawable(context, mUISwitchIcon.getDrawable(), color);
        ColorUtils.applyThemeToDrawable(context, mBookmarkIcon.getDrawable(), color);
        ColorUtils.applyThemeToDrawable(context, mShareIcon.getDrawable(), color);
    }


    private RequestListener<String, GlideDrawable> mainImageLoadListener =
            new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {return false;}

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
                                    ColorUtils.applyThemeToDrawable(ArticleActivity.this,
                                            mBackArrow.getDrawable() ,R.color.dark_icon);
                                    ColorUtils.applyThemeToDrawable(ArticleActivity.this,
                                            mSearchIcon.getDrawable(), R.color.dark_icon);
                                }
                            }
                        });
            }
            return false;
        }
    };

    /**
     * Dismisses any stale toast before making a fresh one
     *
     * @param text         (String) toast text
     * @param longDuration (boolean) long duration
     */
    public void showToast(String text, boolean longDuration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(ArticleActivity.this, text,
                longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }
}
