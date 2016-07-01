package in.medhajnews.app.data.objects;

/**
 * Created by bhav on 6/29/16 for the Medhaj News Project.
 * Base class for all model types
 */
public abstract class DataItem {

    public final long id;
    public final String url;
    public final String title;


    public DataItem(long id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    /**
     * based on id field
     */
    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == getClass() && ((DataItem) obj).id == id);
    }
}
