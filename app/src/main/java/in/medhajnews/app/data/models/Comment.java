package in.medhajnews.app.data.models;

/**
 * Created by bhav on 6/30/16 for the Medhaj News Project.
 */
public class Comment {

    public final long id;
    public final String body;
    public final int userId;

    public Comment(long id, String body, int userId) {
        this.id = id;
        this.body = body;
        this.userId = userId;
    }
}
