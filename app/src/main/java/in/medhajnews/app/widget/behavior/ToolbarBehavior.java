package in.medhajnews.app.widget.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import in.medhajnews.app.R;

/**
 * Created by bhav on 6/23/16 for the Medhaj News Project.
 * Hide on scroll behavior of the toolbar
 */
public class ToolbarBehavior extends CoordinatorLayout.Behavior<AppBarLayout> {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final String TAG = ToolbarBehavior.class.getSimpleName();
    private boolean mIsAnimatingOut = false;

    public ToolbarBehavior(Context context, AttributeSet attr) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes ){
        Log.d(TAG, "onStartNestedScroll: ");
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            child.setExpanded(false, true);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.setExpanded(true, true);
        }
    }

    private void animateOut(final Toolbar toolbar) {
        Animation anim = AnimationUtils.loadAnimation(toolbar.getContext(),
                R.anim.bottom_bar_slide_down);
        anim.setInterpolator(INTERPOLATOR);
        anim.setDuration(200L);
        Log.d(TAG, "animateOut: ");
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                ToolbarBehavior.this.mIsAnimatingOut = true;
            }


            public void onAnimationEnd(Animation animation) {
                ToolbarBehavior.this.mIsAnimatingOut = false;
                toolbar.setVisibility(View.GONE);
            }


            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        toolbar.startAnimation(anim);
    }

    /**
     * animate in will be called for an intro animation from {@link in.medhajnews.app.NewsActivity}
     */
    public void animateIn(final Toolbar toolbar) {
        Animation anim = AnimationUtils.loadAnimation(toolbar.getContext(),
                R.anim.bottom_bar_slide_up);
        anim.setDuration(200L);
        Log.d(TAG, "animateIn: ");
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                toolbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setInterpolator(INTERPOLATOR);
        toolbar.startAnimation(anim);
    }
}
