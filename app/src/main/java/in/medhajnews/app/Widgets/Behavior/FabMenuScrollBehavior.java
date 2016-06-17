package in.medhajnews.app.Widgets.Behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.github.clans.fab.FloatingActionMenu;

import in.medhajnews.app.R;

/**
 * Created by bhav on 6/15/16 for the Medhaj News Project.
 */
public class FabMenuScrollBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu>{
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    public FabMenuScrollBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            this.animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            this.animateIn(child);
        }
    }

    private void animateOut(final FloatingActionMenu button) {
        if(button.isOpened()) {
            button.close(false);
        }
        Animation anim = AnimationUtils.loadAnimation(button.getContext(),
                R.anim.fab_slide_down);
        anim.setInterpolator(INTERPOLATOR);
        anim.setDuration(300L);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                FabMenuScrollBehavior.this.mIsAnimatingOut = true;
            }


            public void onAnimationEnd(Animation animation) {
                FabMenuScrollBehavior.this.mIsAnimatingOut = false;
                button.setVisibility(View.GONE);
            }


            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        button.startAnimation(anim);
    }

    /**
        animate in will be called for an intro animation from {@link in.medhajnews.app.NewsActivity}
     */
    public void animateIn(FloatingActionMenu button) {
        button.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(button.getContext(),
                R.anim.fab_slide_up);
        anim.setDuration(250L);
        anim.setInterpolator(INTERPOLATOR);
        button.startAnimation(anim);}
}
