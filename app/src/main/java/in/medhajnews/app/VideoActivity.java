package in.medhajnews.app;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by bhav on 6/6/16 for the Medhaj News Project.
 */
public class VideoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.video_view);

        VideoView video_view = (VideoView) findViewById(R.id.videoView);
        MediaController mc = new MediaController(this);
        video_view.setVideoURI(Uri.parse("http://jell.yfish.us/media/jellyfish-3-mbps-hd-h264.mkv"));
        video_view.setMediaController(mc);
        //MediaController) findViewById(R.id.mediaController);
        mc.setAnchorView(video_view);
        mc.setMediaPlayer(video_view);
        video_view.start();
    }
}
