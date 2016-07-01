package in.medhajnews.app.widget.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import in.medhajnews.app.R;

/**
 * Created by bhav on 6/16/16 for the Medhaj News Project.
 */
public class BottomBarBehavior extends CoordinatorLayout.Behavior<CardView> {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final String TAG = BottomBarBehavior.class.getSimpleName();
    private boolean mIsAnimatingOut = false;

    public BottomBarBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, CardView child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, CardView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            this.animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            animateIn(child);
        }
    }

    private void animateOut(final CardView card) {
        Animation anim = AnimationUtils.loadAnimation(card.getContext(),
                R.anim.bottom_bar_slide_down);
        anim.setInterpolator(INTERPOLATOR);
        anim.setDuration(200L);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                BottomBarBehavior.this.mIsAnimatingOut = true;
            }


            public void onAnimationEnd(Animation animation) {
                BottomBarBehavior.this.mIsAnimatingOut = false;
                card.setVisibility(View.GONE);
            }


            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        card.startAnimation(anim);
    }

    /**
     * animate in will be called for an intro animation from {@link in.medhajnews.app.NewsActivity}
     */
    public void animateIn(final CardView card) {
        Animation anim = AnimationUtils.loadAnimation(card.getContext(),
                R.anim.bottom_bar_slide_up);
        anim.setDuration(200L);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                card.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setInterpolator(INTERPOLATOR);
        card.startAnimation(anim);
    }
}

