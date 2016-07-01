package in.medhajnews.app.widget.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import in.medhajnews.app.R;

/**
 * Created by bhav on 6/13/16 for the Medhaj News Project.
 * Behavior for the Floating Action Button
 */

public class FabScrollBehavior extends FloatingActionButton.Behavior {
    private static final Interpolator INTERPOLATOR = new OvershootInterpolator();
    private boolean mIsAnimatingOut = false;


    public FabScrollBehavior(Context context, AttributeSet attrs) {
        super();
    }


    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child, final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                        nestedScrollAxes);
    }


    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child, final View target, final int dxConsumed, final int dyConsumed, final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);
        if (dyConsumed > 0 && !this.mIsAnimatingOut &&
                child.getVisibility() == View.VISIBLE) {
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            animateIn(child);
        }
    }

    private void animateOut(final FloatingActionButton button) {
        Animation anim = AnimationUtils.loadAnimation(button.getContext(),
                R.anim.fab_slide_down);
        anim.setInterpolator(INTERPOLATOR);
        anim.setDuration(200L);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                FabScrollBehavior.this.mIsAnimatingOut = true;
            }


            public void onAnimationEnd(Animation animation) {
                FabScrollBehavior.this.mIsAnimatingOut = false;
                button.setVisibility(View.GONE);
            }


            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        button.startAnimation(anim);
    }

    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(button.getContext(),
                R.anim.fab_slide_up);
        anim.setDuration(200L);
        anim.setInterpolator(INTERPOLATOR);
        button.startAnimation(anim);
    }
}
