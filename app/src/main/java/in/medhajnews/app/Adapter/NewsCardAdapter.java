package in.medhajnews.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.medhajnews.app.ArticleActivity;
import in.medhajnews.app.NewsActivity;
import in.medhajnews.app.Objects.Article;
import in.medhajnews.app.R;

/**
 * Created by bhav on 6/9/16 for the Medhaj News Project.
 */
public class NewsCardAdapter extends RecyclerView.Adapter<NewsCardAdapter.NewsCardsHolder> {

    public static final String TAG = NewsCardAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Article> mArticleList = new ArrayList<Article>();
    private boolean mTwoViewType;

    public NewsCardAdapter(Context context, ArrayList<Article> articles, boolean isRecommendedSection) {
        this.mContext = context;
        this.mArticleList = articles;
        this.mTwoViewType = isRecommendedSection;
    }

    @Override
    public NewsCardsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.newscard,
                parent, false);
        return new NewsCardsHolder(myItem);
    }

//    public void updateItems(boolean animated) {
//        mArticleList.clear();
//        for(int i = 0; i< 25; i++) {
//            mArticleList.add(Article.sampleArticle(mContext));
//        }
//        if (animated) {
//            notifyItemRangeInserted(0 , mArticleList.size());
//        } else {
//            notifyDataSetChanged();
//        }
//        MainFragment.DummyArticleList.addAll(mArticleList);
//    }

    @Override
    public void onBindViewHolder(final NewsCardsHolder holder, int position) {
        final Article currentArticle = mArticleList.get(position);
//        holder.cardCategory.setText(currentArticle.Category);
        holder.cardTitle.setText(currentArticle.ArticleTitle);

        Glide.with(mContext).load(Uri.parse(currentArticle.ArticleImageLink))
                .placeholder(R.color.black).into(holder.cardImage);
        Log.d(TAG, "onBindViewHolder: " + currentArticle.ArticleImageLink);

        //get article save state into card
        if(Boolean.parseBoolean(currentArticle.isArticleSaved)) {
            holder.cardSaveButton.setImageResource(R.drawable.ic_saved_offline);
        } else {
            holder.cardSaveButton.setImageResource(R.drawable.ic_save_offline);
        }

        //change save state onclick
        holder.cardSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NewsActivity.mFloatingActionMenu.isOpened()) {
                    NewsActivity.mFloatingActionMenu.close(true);
                } else {
                    if (Boolean.parseBoolean(currentArticle.isArticleSaved)) {
                        currentArticle.saveArticle(currentArticle);
                        holder.cardSaveButton.setImageResource(R.drawable.ic_saved_offline);
                    } else {
                        currentArticle.unsaveArticle(currentArticle);
                        holder.cardSaveButton.setImageResource(R.drawable.ic_save_offline);
                    }
                }
            }
        });

        //bring up article onclick
        holder.newsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NewsActivity.mFloatingActionMenu.isOpened()) {
                    NewsActivity.mFloatingActionMenu.close(true);
                } else {
                    Intent article = new Intent(mContext, ArticleActivity.class);
                    article.putExtra("Article", currentArticle);
                    ((Activity) mContext).startActivity(article);
                }
            }
        });

        //share button
        holder.cardShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewsActivity.mFloatingActionMenu.isOpened()) {
                    NewsActivity.mFloatingActionMenu.close(true);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ((AnimatedVectorDrawable) holder.cardShareButton.getDrawable()).start();
                    }
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    String shareBody = currentArticle.ArticleTitle + "\n" + currentArticle.ArticleLink;
                    String shareSubject = "Shared via Meddhaj News App";
                    share.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                    share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    ((Activity) mContext).startActivity(Intent.createChooser(share, "Share via"));
                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    public static class NewsCardsHolder extends RecyclerView.ViewHolder {

        public ImageView cardImage;
        public TextView cardTitle;
//        public TextView cardCategory;
        public ImageView cardSaveButton;
        public ImageView cardShareButton;
        public CardView newsCard;

        public NewsCardsHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            cardTitle = (TextView) itemView.findViewById(R.id.title);
//            cardCategory = (TextView) itemView.findViewById(R.id.card_category);
            cardSaveButton = (ImageView) itemView.findViewById(R.id.save);
            newsCard = (CardView) itemView.findViewById(R.id.news_card);
            cardShareButton = (ImageView) itemView.findViewById(R.id.share);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                cardShareButton.setImageResource(R.drawable.avd_share);
            }

        }
    }

    public static class TagHolder extends RecyclerView.ViewHolder {

        public TagHolder(View itemView) {
            super(itemView);

        }
    }
}
