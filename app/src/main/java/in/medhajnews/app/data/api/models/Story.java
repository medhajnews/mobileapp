package in.medhajnews.app.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import in.medhajnews.app.data.objects.DataItem;

/**
 * Created by bhav on 6/3/16 for the Medhaj News Project.
 * news article parcelable model
 */
public class Story extends DataItem implements Parcelable {

    public final static String INTENT_EXTRA = Story.class.getSimpleName();

    public final String author;
    public final String date;
    public final String time;
    public final String category;
    public final String language;
    public final List<String> link_image; //todo : add support for multi photo stories in article view
    public final String area;
    public final int likes;
    public final int dislikes;
    public final String content;
    public final List<Comment> comments;
    public final String type;

    public boolean isSaved = false;
    public boolean hasViewed = false;
    //todo : move to item decorator, recyclerView may kill view view accidentally
    public boolean hasAnimated = false;

    public Story(long id,
                 String url,
                 String title,
                 String author,
                 String date,
                 String time,
                 String category,
                 String language,
                 List<String> link_image,
                 String area,
                 int likes,
                 int dislikes,
                 String content,
                 List<Comment> comments,
                 String type) {
        super(id, url, title);
        this.author = author;
        this.date = date;
        this.time = time;
        this.category = category;
        this.language = language;
        this.link_image = link_image;
        this.area = area;
        this.likes = likes;
        this.dislikes = dislikes;
        this.content = content;
        this.comments = comments;
        this.type = type;
    }

    protected Story(Parcel in) {
        super(in.readLong(), in.readString(), in.readString());
        author = in.readString();
        date = in.readString();
        time = in.readString();
        category = in.readString();
        language = in.readString();
        link_image = in.createStringArrayList();
        area = in.readString();
        likes = in.readInt();
        dislikes = in.readInt();
        content = in.readString();
        if(in.readByte() == 0x01) {
            comments = new ArrayList<Comment>();
            in.readList(comments, Comment.class.getClassLoader());
        } else {
            comments = null;
        }
        isSaved = in.readByte() != 0;
        hasAnimated = in.readByte() != 0;
        type = in.readString();
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(url);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(category);
        parcel.writeString(language);
        parcel.writeStringList(link_image);
        parcel.writeString(area);
        parcel.writeInt(likes);
        parcel.writeInt(dislikes);
        parcel.writeString(content);
        if(comments == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeList(comments);
        }
        parcel.writeByte((byte) (isSaved ? 1 : 0));
        parcel.writeByte((byte) (hasAnimated ? 1 : 0));
        parcel.writeString(type);
    }
}