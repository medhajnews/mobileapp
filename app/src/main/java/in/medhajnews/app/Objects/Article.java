package in.medhajnews.app.Objects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import in.medhajnews.app.R;

/**
 * Created by bhav on 6/3/16 for the Medhaj News Project.
 */
//// TODO: 6/11/16 : add Json Annotations
public class Article implements Parcelable{

    public String ArticleContent;
    public String ArticleAuthor;
    public String ArticleTitle;
    public String ArticleDate;
    public String ArticleUpdateTime;
    public String Category;
    public String isArticleSaved;
    public String ArticleImageLink;
    public String ArticleLink;
    public String ArticleArea;

    public Article(String articleContent, String articleAuthor, String articleTitle, String articleDate,
                   String articleUpdateTime, String category, boolean isSaved, String articleImageLink,
                   String articleLink, String articleArea) {
        this.ArticleAuthor = articleAuthor;
        this.ArticleContent = articleContent;
        this.ArticleDate = articleDate;
        this.ArticleTitle = articleTitle;
        this.ArticleUpdateTime = articleUpdateTime;
        this.Category = category;
        this.isArticleSaved = String.valueOf(isSaved);
        this.ArticleImageLink = articleImageLink;
        this.ArticleLink = articleLink;
        this.ArticleArea = articleArea;
    }

    protected Article(Parcel in) {
        ArticleContent = in.readString();
        ArticleAuthor = in.readString();
        ArticleTitle = in.readString();
        ArticleDate = in.readString();
        ArticleUpdateTime = in.readString();
        Category = in.readString();
        isArticleSaved = in.readString();
        ArticleImageLink = in.readString();
        ArticleLink = in.readString();
        ArticleArea = in.readString();
    }

    public void saveArticle(Article article) {
        article.isArticleSaved = String.valueOf(true);
    }

    public void unsaveArticle(Article article){
        article.isArticleSaved = String.valueOf(false);
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
                context.getString(R.string.sample_content_text),
                context.getString(R.string.sample_author),
                context.getString(R.string.sample_title),
                context.getString(R.string.sample_date),
                context.getString(R.string.sample_update_time),
                context.getString(R.string.sample_category),
                false,
                context.getString(R.string.sample_image_link),
                context.getString(R.string.sample_article_link),
                context.getString(R.string.sample_area)
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
        dest.writeString(isArticleSaved);
        dest.writeString(ArticleImageLink);
        dest.writeString(ArticleLink);
        dest.writeString(ArticleArea);
    }
}
