package in.medhajnews.app.data.api;

import java.util.List;

import in.medhajnews.app.data.api.models.Advert;
import in.medhajnews.app.data.api.models.Photo;
import in.medhajnews.app.data.api.models.Story;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface NewsService {
    String ENDPOINT = "https://tranquil-dusk-46393.herokuapp.com";

    @GET("latest")
    @Headers({
            "Accept: application/json",
            "User-Agent: Android App"
    })
    Call<List<Story>> getStories();

    @GET("ads")
    @Headers({
            "Accept: application/json",
            "User-Agent: Android App"
    })
    Call<List<Advert>> getAds();

    @GET("photos")
    @Headers({
            "Accept: application/json",
            "User-Agent: Android App"
    })
    Call<List<Photo>> getPhotos();

}