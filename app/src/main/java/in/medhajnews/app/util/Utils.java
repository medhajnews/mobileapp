package in.medhajnews.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;

import in.medhajnews.app.data.api.models.Story;
import in.medhajnews.app.R;

/**
 * Created by bhav on 6/13/16 for the Medhaj News Project.
 * Use this class for all misc static methods
 */
public class Utils {
    public static final String SHARED_PREFS = "MedhajPreferences";
    /**
     * calculates Toolbar height for animating toolbar
     * @param context (Context) from calling Activity
     * @return (int) height of toolbar
     */
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    @NonNull
    public static String parseCategory(@NonNull Story a) {
        if(a.category.toLowerCase().contains("india")) return "India";
        else
            if (a.category.toLowerCase().contains("world")) return "World";
        else
            if (a.category.toLowerCase().contains("science")) return "Science";
        else return "ignore"; //ignore categories
    }

    public static void rankCategory(Context context, String category) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(category, prefs.getInt(category,0)+1);
        editor.apply();
    }

}
