package in.medhajnews.app.data.api.models;

import in.medhajnews.app.data.objects.DataItem;

/**
 * Created by bhav on 6/29/16 for the Medhaj News Project.
 * sponsoredArticle/ads parcelable model
 * extending AppItem for AdModel
 */
public class Advert extends DataItem{

    public final String provider;
    public final String link_image;
    public final String content;


    public Advert(long id,
                  String url,
                  String title,
                  String provider,
                  String link_image,
                  String content) {
        super(id, url, title);
        this.provider = provider;
        this.link_image = link_image;
        this.content = content;
    }
}
