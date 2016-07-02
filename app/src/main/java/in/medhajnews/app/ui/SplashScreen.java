package in.medhajnews.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import in.medhajnews.app.R;
import in.medhajnews.app.data.CacheDBHelper;

/**
 * Created by bhav on 6/4/16 for the Medhaj News Project.
 */
public class SplashScreen extends AppCompatActivity {

    private CacheDBHelper mCacheDBHelper;
    private static final String TAG = SplashScreen.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView appLogo = (ImageView) findViewById(R.id.app_logo);
        ImageView appIcon = (ImageView) findViewById(R.id.app_icon);

        Glide.with(this).load(R.drawable.newslogo).crossFade().fitCenter().into(appLogo);
        Glide.with(this).load(R.mipmap.ic_launcher).crossFade().into(appIcon);

        mCacheDBHelper = new CacheDBHelper(this);

        //todo : edit on switching API
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://tranquil-dusk-46393.herokuapp.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

//        NewsService fetcher = retrofit.create(NewsService.class);
//        Call<List<DataItem>> call = fetcher.getData();

////        call.enqueue(new Callback<ArrayList<Story>>() {
////            @Override
////            public void onResponse(Call<ArrayList<Story>> call, Response<ArrayList<Story>> response) {
////                if(response.isSuccessful()) {
//////                    for(Story a : response.body()) {
////////                        mCacheDBHelper.addArticle(a);
//////                    }
////                    exitSplash(SplashScreen.this, true);
////                } else {
////                    exitSplash(SplashScreen.this, false);
////                }
////            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Story>> call, Throwable t) {
//                t.printStackTrace();
//                exitSplash(SplashScreen.this, false);
//            }
//        });
    }

    public static void exitSplash(Context context, boolean isInternetAvailable) {
        Intent main = new Intent(context, NewsActivity.class);
        main.putExtra("internet_available", isInternetAvailable);
        context.startActivity(main);
        ((Activity) context).finish();
    }
}
