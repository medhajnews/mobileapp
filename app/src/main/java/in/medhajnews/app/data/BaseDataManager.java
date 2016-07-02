package in.medhajnews.app.data;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import in.medhajnews.app.data.api.NewsService;
import in.medhajnews.app.data.api.SearchService;
import in.medhajnews.app.data.prefs.MedhajPrefs;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bhav on 7/1/16 for the Medhaj News Project.
 * Base class for data loading
 */
public abstract class BaseDataManager<T> implements DataLoadingSubject {

    private final AtomicInteger loadingCount;
    private final MedhajPrefs medhajPrefs;
    private SearchService searchApi;
    private List<DataLoadingCallbacks> loadingCallbacks;

    protected BaseDataManager(@NonNull Context context) {
        loadingCount = new AtomicInteger(0);
        medhajPrefs = MedhajPrefs.get(context);
    }

    public abstract void onDataLoaded(T data);

    public abstract void cancelLoading();

    @Override
    public boolean isLoading() {
        return loadingCount.get() > 0;
    }

    public MedhajPrefs getMedhajPrefs() {
        return medhajPrefs;
    }

    public NewsService getNewsApi() {
        return medhajPrefs.getApi();
    }

    @Override
    public void registerCallback(DataLoadingCallbacks callbacks) {
        if(loadingCallbacks == null) {
            loadingCallbacks = new ArrayList<>(1);
        }
        loadingCallbacks.add(callbacks);
    }

    @Override
    public void unregisterCallback(DataLoadingCallbacks callbacks) {
        if(loadingCallbacks != null && loadingCallbacks.contains(callbacks)) {
            loadingCallbacks.remove(callbacks);
        }
    }

    protected void loadStarted() {
        if (0 == loadingCount.getAndIncrement()) {
            dispatchLoadingStartedCallbacks();
        }
    }

    protected void loadFinished() {
        if (0 == loadingCount.decrementAndGet()) {
            dispatchLoadingFinishedCallbacks();
        }
    }

    protected void resetLoadingCount() {
        loadingCount.set(0);
    }

    protected void dispatchLoadingStartedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (DataLoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.loadingStarted();
        }
    }

    protected void dispatchLoadingFinishedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (DataLoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.loadingFinished();
        }
    }

    private void createSearchApi() {
        searchApi = new Retrofit.Builder()
                .baseUrl(SearchService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SearchService.class);
    }

}
