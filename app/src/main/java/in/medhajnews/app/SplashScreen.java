package in.medhajnews.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.InetAddress;

/**
 * Created by bhav on 6/4/16 for the Medhaj News Project.
 */
public class SplashScreen extends AppCompatActivity {

    private NetworkingThread networkingThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView applogo = (ImageView) findViewById(R.id.app_logo);
        ImageView appicon = (ImageView) findViewById(R.id.app_icon);

        Glide.with(this).load(R.drawable.logo_128px).crossFade().fitCenter().into(applogo);
        Glide.with(this).load(R.mipmap.ic_launcher).crossFade().into(appicon);

        networkingThread = new NetworkingThread(SplashScreen.this);
        networkingThread.execute();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    private class NetworkingThread extends AsyncTask<Void, Void, Void> {

        private String TAG = NetworkingThread.class.getSimpleName();
        private boolean isInternetWorking = false;
        private Context mContext;

        NetworkingThread(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            // replace with useful code
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // end replace
            if(isNetworkConnected()) {
                isInternetWorking = isInternetAvailable();
            } else {
                isInternetWorking = false;
            }
            Log.d(TAG, String.valueOf(isInternetWorking));
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            exitSplash(mContext, isInternetWorking);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop background tasks
        if(networkingThread!=null) {
            if (networkingThread.getStatus() != AsyncTask.Status.FINISHED) {
                networkingThread.cancel(true);
            }
        }
    }

    public static void exitSplash(Context context, boolean isInternetAvailble) {
        Intent main = new Intent(context, MainActivity.class);
        main.putExtra("internet_available", isInternetAvailble);
        context.startActivity(main);
        ((Activity) context).finish();
    }
}
