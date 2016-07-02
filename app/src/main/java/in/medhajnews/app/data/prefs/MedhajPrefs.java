package in.medhajnews.app.data.prefs;

import android.content.Context;

import in.medhajnews.app.data.api.NewsService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bhav on 7/1/16 for the Medhaj News Project.
 * Manages user state
 */
public class MedhajPrefs {

    //todo : complete after final api
    private NewsService api;
    private boolean isLoggedIn= false;
    private long userId;
    private static volatile MedhajPrefs singleton;

    public MedhajPrefs(Context context) {

    }

    public static MedhajPrefs get(Context context) {
        if(singleton == null) {
            synchronized (MedhajPrefs.class) {
                singleton = new MedhajPrefs(context);
            }
        }
        return singleton;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public NewsService getApi() {
        if (api == null) createApi();
        return api;
    }

    private void createApi() {
        api = new Retrofit.Builder()
                .baseUrl(NewsService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsService.class);
    }
}
