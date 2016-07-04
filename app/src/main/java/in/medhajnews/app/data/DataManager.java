package in.medhajnews.app.data;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.medhajnews.app.data.api.models.Advert;
import in.medhajnews.app.data.api.models.Photo;
import in.medhajnews.app.data.api.models.Story;
import in.medhajnews.app.data.objects.DataItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bhav on 7/1/16 for the Medhaj News Project.
 * Responsible for loading data
 */
public abstract class DataManager extends BaseDataManager<List<? extends DataItem>> {

    private Map<String, Call> callMap;
    private final static String TAG = DataManager.class.getSimpleName();
    private final static String ADS = "ADS";
    private final static String STORIES = "STORIES";
    private final static String PHOTOS = "PHOTOS"; //photo articles, i didn't think before naming
                                                    // todo : refactor code


    public DataManager(@NonNull Context context) {
        super(context);
        callMap = new HashMap<>();
    }


    public void loadData() {
        loadStarted();
        loadTopStories();
        loadAds();
        loadImageStories();
    }

    @Override
    public void cancelLoading() {
        if(callMap.size() > 0) {
            for(Call call : callMap.values()) {
                call.cancel();
            }
            callMap.clear();
        }
    }

    private void loadFinished(String key) {
        if (callMap.size() > 1) { // finish loading after removing last item
            callMap.remove(key);
            loadFinished();
        }
        callMap.remove(key);
    }

    private void loadFailed(String key) {
        loadFailed();
        cancelLoading();  //cancel all calls if one call fails, //todo update this with a better logic
    }

    private void loadTopStories() {
        final Call<List<Story>> topStories = getNewsApi().getStories();
        topStories.enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null && !response.body().isEmpty()) {
                        loadFinished(STORIES);
                        onDataLoaded(response.body());
                    }
                } else {
                    loadFailed(STORIES);
                }
            }

            @Override
            public void onFailure(Call<List<Story>> call, Throwable t) {
                loadFailed(STORIES);
            }
        });
        callMap.put(STORIES, topStories);
    }

    private void loadImageStories() {
        final Call<List<Photo>> topImages = getNewsApi().getPhotos();
        topImages.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null && !response.body().isEmpty()) {
                        loadFinished(PHOTOS);
                        onDataLoaded(response.body());
                    }
                } else {
                    loadFailed(PHOTOS);
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                loadFailed(PHOTOS);
            }
        });
        callMap.put(PHOTOS, topImages);
    }

    private void loadAds() {
        final Call<List<Advert>> ads = getNewsApi().getAds();
        ads.enqueue(new Callback<List<Advert>>() {
            @Override
            public void onResponse(Call<List<Advert>> call, Response<List<Advert>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null && !response.body().isEmpty()) {
                        loadFinished(ADS);
                        onDataLoaded(response.body());
                    }
                } else {
                    loadFailed(ADS);
                }
            }

            @Override
            public void onFailure(Call<List<Advert>> call, Throwable t) {
                loadFailed(ADS);
            }
        });
        callMap.put(ADS, ads);
    }
}
