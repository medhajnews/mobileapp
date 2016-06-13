package in.medhajnews.app;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by bhav on 6/13/16 for the Medhaj News Project.
 * Use this class for all misc static methods
 */
public class Utils {
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}
