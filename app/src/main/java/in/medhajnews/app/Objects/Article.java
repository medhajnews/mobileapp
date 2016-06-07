package in.medhajnews.app.Objects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import in.medhajnews.app.R;

/**
 * Created by bhav on 6/3/16 for the Medhaj News Project.
 */
public class Article implements Parcelable{

    private String ArticleContent;
    private String ArticleAuthor;
    private String ArticleTitle;
    private String ArticleDate;
    private String ArticleUpdateTime;
    private String Category;

    public Article(String articleContent, String articleAuthor, String articleTitle, String articleDate,
                   String articleUpdateTime, String category) {
        this.ArticleAuthor = articleAuthor;
        this.ArticleContent = articleContent;
        this.ArticleDate = articleDate;
        this.ArticleTitle = articleTitle;
        this.ArticleUpdateTime = articleUpdateTime;
        this.Category = category;
    }

    protected Article(Parcel in) {
        ArticleContent = in.readString();
        ArticleAuthor = in.readString();
        ArticleTitle = in.readString();
        ArticleDate = in.readString();
        ArticleUpdateTime = in.readString();
        Category = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public static Article sampleArticle(Context context) {

        return new Article(
                context.getResources().getString(R.string.sample_content_text),
                context.getResources().getString(R.string.sample_author),
                context.getResources().getString(R.string.sample_title),
                context.getResources().getString(R.string.sample_date),
                context.getResources().getString(R.string.sample_update_time),
                context.getString(R.string.sample_category)
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ArticleContent);
        dest.writeString(ArticleAuthor);
        dest.writeString(ArticleTitle);
        dest.writeString(ArticleDate);
        dest.writeString(ArticleUpdateTime);
        dest.writeString(Category);
    }
}
