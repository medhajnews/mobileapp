package in.medhajnews.app.Widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by bhav on 6/3/16 for the Medhaj News Project.
 */
public class FontSelectionSeekBar extends SeekBar {

    private Paint mPaint = new Paint();
    public FontSelectionSeekBar(Context context) {
        super(context);
    }

    public FontSelectionSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FontSelectionSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FontSelectionSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int thumb_x = (int) (( (double)this.getProgress()/this.getMax() ) * (double)this.getWidth()/2);
        int middle = this.getHeight()/2;
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(32);
        canvas.drawText("Hello", thumb_x, middle - 40, mPaint);
    }
}
