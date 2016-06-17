package in.medhajnews.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ShareActivity extends AppCompatActivity {

    FloatingActionButton mFloatingActionButton;
    private boolean mBarOpen = false;
    private Animation barAnimation;
    private CardView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mBottomBar = (CardView) findViewById(R.id.bottom_bar);

        ImageView color = (ImageView) findViewById(R.id.color);
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(ShareActivity.this, CameraActivity.class);
                startActivity(camera);
            }
        });

        barAnimation = AnimationUtils.loadAnimation(this, R.anim.bar_share);
        final Animation fabAnimation = AnimationUtils.loadAnimation(this, R.anim.share_fab);

        barAnimation.setDuration(200L);
        fabAnimation.setDuration(200L);

        fabAnimation.setInterpolator(new FastOutSlowInInterpolator());
        barAnimation.setInterpolator(new FastOutSlowInInterpolator());

        fabAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFloatingActionButton.setVisibility(View.GONE);
                mBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        barAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFloatingActionButton.setVisibility(View.VISIBLE);
                mBottomBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionButton.startAnimation(fabAnimation);
                mBarOpen = true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(mBarOpen) {
            mBottomBar.startAnimation(barAnimation);
            mBarOpen = false;
        } else {
            super.onBackPressed();
        }
    }
}
