package in.medhajnews.app.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;

import in.medhajnews.app.R;

/**
 * A view group that draws a badge drawable on top of it's contents.
 */
public class BadgedFourThreeImageView extends FourThreeImageView {

    private Drawable badge;
    private boolean drawBadge;
    private boolean badgeBoundsSet = false;
    private int badgeGravity;
    private int badgePadding;

    public BadgedFourThreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        badge = new AdBadge(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgedImageView, 0, 0);
        badgeGravity = a.getInt(R.styleable.BadgedImageView_badgeGravity, Gravity.END | Gravity
                .BOTTOM);
        badgePadding = a.getDimensionPixelSize(R.styleable.BadgedImageView_badgePadding, 0);
        a.recycle();

    }

    public void showBadge(boolean show) {
        drawBadge = show;
    }

    public void setBadgeColor(@ColorInt int color) {
        badge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (drawBadge) {
            if (!badgeBoundsSet) {
                layoutBadge();
            }
            badge.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutBadge();
    }

    private void layoutBadge() {
        Rect badgeBounds = badge.getBounds();
        Gravity.apply(badgeGravity,
                badge.getIntrinsicWidth(),
                badge.getIntrinsicHeight(),
                new Rect(0, 0, getWidth(), getHeight()),
                badgePadding,
                badgePadding,
                badgeBounds);
        badge.setBounds(badgeBounds);
        badgeBoundsSet = true;
    }

    /**
     * A drawable for indicating that an image is animated
     */
    private static class AdBadge extends Drawable {

        private static final String AD = "AD";
        private static final int TEXT_SIZE = 12;    // sp
        private static final int PADDING = 4;       // dp
        private static final int CORNER_RADIUS = 2; // dp
        private static final int BACKGROUND_COLOR = Color.WHITE;
        private static final String TYPEFACE = "sans-serif-black";
        private static final int TYPEFACE_STYLE = Typeface.NORMAL;
        private static Bitmap bitmap;
        private static int width;
        private static int height;
        private final Paint paint;

        AdBadge(Context context) {
            if (bitmap == null) {
                final DisplayMetrics dm = context.getResources().getDisplayMetrics();
                final float density = dm.density;
                final float scaledDensity = dm.scaledDensity;
                final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint
                        .SUBPIXEL_TEXT_FLAG);
                textPaint.setTypeface(Typeface.create(TYPEFACE, TYPEFACE_STYLE));
                textPaint.setTextSize(TEXT_SIZE * scaledDensity);

                final float padding = PADDING * density;
                final float cornerRadius = CORNER_RADIUS * density;
                final Rect textBounds = new Rect();
                textPaint.getTextBounds(AD, 0, AD.length(), textBounds);
                height = (int) (padding + textBounds.height() + padding);
                width = (int) (padding + textBounds.width() + padding);
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setHasAlpha(true);
                final Canvas canvas = new Canvas(bitmap);
                final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                backgroundPaint.setColor(BACKGROUND_COLOR);
                //todo : add rounded rectangles for all API
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(0, 0, width, height, cornerRadius, cornerRadius,
                            backgroundPaint);
                } else {
                    canvas.drawRect(0, 0, width, height, backgroundPaint);
                }
                // punch out the word 'GIF', leaving transparency
                textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                canvas.drawText(AD, padding, height - padding, textPaint);
            }
            paint = new Paint();
        }

        @Override
        public int getIntrinsicWidth() {
            return width;
        }

        @Override
        public int getIntrinsicHeight() {
            return height;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawBitmap(bitmap, getBounds().left, getBounds().top, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            // ignored
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }
}
