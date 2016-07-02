package in.medhajnews.app.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import in.medhajnews.app.data.objects.DataItem;

/**
 * Created by bhav on 6/30/16 for the Medhaj News Project.
 */
public class Photo extends DataItem implements Parcelable{

    public final static String INTENT_EXTRA = Photo.class.getSimpleName();

    public final List<String> link_image;
    public final List<String> content;
    public final String author;
    public final String category;
    public final String language;
    public final String date;
    public final String time;
    public final int likes;
    public final int dislikes;
    public final List<Comment> comments;

    public boolean hasAnimated = false;
    public boolean hasViewed = false;

    public Photo(long id,
                 String url,
                 String title,
                 List<String> link_image,
                 List<String> content,
                 String author,
                 String category,
                 String language,
                 String date,
                 String time,
                 int likes,
                 int dislikes,
                 List<Comment> comments) {
        super(id, url, title);
        this.link_image = link_image;
        this.content = content;
        this.author = author;
        this.category = category;
        this.language = language;
        this.date = date;
        this.time = time;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
    }

    protected Photo(Parcel in) {
        super(in.readLong(), in.readString(), in.readString());
        link_image = in.createStringArrayList();
        content = in.createStringArrayList();
        author = in.readString();
        category = in.readString();
        language = in.readString();
        date = in.readString();
        time = in.readString();
        likes = in.readInt();
        dislikes = in.readInt();
        if(in.readByte() == 0x01) {
            comments = new ArrayList<Comment>();
            in.readList(comments, Comment.class.getClassLoader());
        } else {
            comments = null;
        }
        hasAnimated = in.readByte() != 0;
        hasViewed = in.readByte() != 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
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
        parcel.writeStringList(link_image);
        parcel.writeStringList(content);
        parcel.writeString(author);
        parcel.writeString(category);
        parcel.writeString(language);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeInt(likes);
        parcel.writeInt(dislikes);
        if(comments == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeList(comments);
        }
        parcel.writeByte((byte) (hasAnimated ? 1 : 0));
        parcel.writeByte((byte) (hasViewed ? 1 : 0));
    }
}
