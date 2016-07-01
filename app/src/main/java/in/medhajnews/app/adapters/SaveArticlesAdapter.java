package in.medhajnews.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import in.medhajnews.app.ArticleActivity;
import in.medhajnews.app.NewsActivity;
import in.medhajnews.app.R;
import in.medhajnews.app.data.models.Story;

/**
 * Created by bhav on 6/23/16 for the Medhaj News Project.
 */
public class SaveArticlesAdapter extends RecyclerView.Adapter<SaveArticlesAdapter.SaveArticlesHolder> {

    public ArrayList<Story> mSavedStories;
    private final static String TAG = SaveArticlesAdapter.class.getSimpleName();
    private boolean isListEmpty = true;
    private Context mContext;

    public SaveArticlesAdapter(ArrayList<Story> savedStories, Context context) {
            this.mSavedStories = savedStories;
            isListEmpty = false;
        this.mContext = context;
    }

    @Override
    public SaveArticlesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SaveArticlesHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.item_info_card, parent, false));
    }

    @Override
    public void onBindViewHolder(final SaveArticlesHolder holder, int position) {
            final Story currentStory = mSavedStories.get(position);
            holder.cardTitle.setText(currentStory.title);
            Glide.with(mContext)
                    .load(Uri.parse(currentStory.url))
                    .placeholder(R.color.black)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.cardImage);

        holder.likeCount.setText(String.valueOf(currentStory.likes));

            holder.newsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NewsActivity.mFloatingActionMenu.isOpened()) {
                        NewsActivity.mFloatingActionMenu.close(true);
                    } else {
                        mContext.startActivity(new Intent(mContext, ArticleActivity.class)
                                .putExtra("Story", currentStory));
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return mSavedStories.size();
    }

    public class SaveArticlesHolder extends RecyclerView.ViewHolder {

        public ImageView cardImage;
        public TextView cardTitle;
        public CardView newsCard;
        public TextView likeCount;

        public SaveArticlesHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            cardTitle = (TextView) itemView.findViewById(R.id.title);
            newsCard = (CardView) itemView.findViewById(R.id.news_card);
            likeCount = (TextView) itemView.findViewById(R.id.like_count);
        }
    }
}
