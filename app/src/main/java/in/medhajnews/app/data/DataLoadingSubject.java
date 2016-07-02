package in.medhajnews.app.data;

/**
 * Created by bhav on 7/1/16 for the Medhaj News Project.
 * interface to observe data load
 */
public interface DataLoadingSubject {
    boolean isLoading();
    void registerCallback(DataLoadingCallbacks callbacks);
    void unregisterCallback(DataLoadingCallbacks callbacks);

    interface DataLoadingCallbacks {
        void loadingStarted();
        void loadingFinished();
    }
}
