package in.medhajnews.app.data.api.models;

/**
 * Created by bhav on 6/30/16 for the Medhaj News Project.
 */
public class User {

    public final long id;
    public final String name;
    public final String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
