package in.medhajnews.app.interfaces;

import java.util.List;

import in.medhajnews.app.data.models.Advert;
import in.medhajnews.app.data.models.Story;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface DataFetcher {
    String ENDPOINT = "https://tranquil-dusk-46393.herokuapp.com";

    @GET("latest")
    @Headers({
            "Accept: application/json",
            "User-Agent: Android App"
    })
    Call<List<Story>> getArticles();

    @GET("ads")
    @Headers({
            "Accept: application/json",
            "User-Agent: Android App"
    })
    Call<List<Advert>> getAds();

}