package in.medhajnews.app.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import in.medhajnews.app.ArticleActivity;
import in.medhajnews.app.R;
import in.medhajnews.app.data.ArticleDBHelper;
import in.medhajnews.app.data.objects.DataItem;
import in.medhajnews.app.data.models.Advert;
import in.medhajnews.app.data.models.Story;
import in.medhajnews.app.utils.ObservableColorMatrix;
import in.medhajnews.app.widget.BadgedFourThreeImageView;

/**
 * Created by bhav on 6/9/16 for the Medhaj News Project.
 */
//todo : recyclerview.holder
public class NewsCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = NewsCardAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_LARGE = 1;
    private static final int VIEW_TYPE_NORMAL = 2;
    private static final int VIEW_TYPE_CONDENSED = 3;
    private static final int VIEW_TYPE_STUB = 4;
    // use VIEW_TYPE advertisement to insert ads into the recyclerview
    private static final int VIEW_TYPE_ADVERTISEMENT = 9;

    private ArticleDBHelper mArticleHelper;



    private Context mContext;
    public List<DataItem> mLoadList;
    private boolean showCategory;
    private LayoutInflater layoutInflater;

    public NewsCardAdapter(Context context, List<DataItem> items, boolean isRecommendedSection) {
        this.mContext = context;
        this.mLoadList = items;
        this.showCategory = isRecommendedSection;
        layoutInflater = LayoutInflater.from(context);
        mArticleHelper = new ArticleDBHelper(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ADVERTISEMENT :
                return createAdHolder(parent);
            case VIEW_TYPE_LARGE :
                return createLargeCardHolder(parent);
            case VIEW_TYPE_NORMAL :
                return createCardHolder(parent);
            case VIEW_TYPE_CONDENSED :
                return createCondensedCardHolder(parent);
            case VIEW_TYPE_STUB :
                return createStubHolder(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case VIEW_TYPE_ADVERTISEMENT :
                bindAdvertisementView((Advert) mLoadList.get(position), (AdHolder) holder);
                break;
            case VIEW_TYPE_CONDENSED :
                bindCondensedCardView((Story) mLoadList.get(position), (CondensedCardHolder) holder);
                break;
            case VIEW_TYPE_LARGE :
                bindLargeCardView((Story) mLoadList.get(position), (LargeCardHolder) holder);
                break;
            case VIEW_TYPE_NORMAL :
                bindCardView((Story) mLoadList.get(position), (CardHolder) holder);
                break;
            case VIEW_TYPE_STUB :
                bindStubView((StubHolder) holder);
                break;
        }
    }

    private int getDataItemCount() {
        return mLoadList.size();
    }

    private DataItem getItemAt(int position) {
        return mLoadList.get(position);
    }

    private boolean smallArticle(int position) {
        return ((Story) mLoadList.get(position)).title.length() <= 32;
    }

    @Override
    public int getItemViewType(int position) {
        if(position < getDataItemCount() && getDataItemCount() > 0) {
            if(getItemAt(position) instanceof Advert) {
                return VIEW_TYPE_ADVERTISEMENT;
            } else if(getItemAt(position) instanceof Story) {
                if(position==0) {
                    return VIEW_TYPE_LARGE;
                } else if (smallArticle(position)) {
                    return VIEW_TYPE_CONDENSED;
                } else {
                    return VIEW_TYPE_NORMAL;
                }
            }
        }
        return VIEW_TYPE_STUB;
    }

    @Override
    public int getItemCount() {
        return mLoadList.size() == 0 ? 0 : mLoadList.size() + 1;
    }

    public static class CardHolder extends RecyclerView.ViewHolder {

        public ImageView cardImage;
        public TextView cardTitle;
        public TextView cardCategory;
        public ImageView cardSaveButton;
        public LinearLayout newsBase;
        private TextView likesCount;

        public CardHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            cardTitle = (TextView) itemView.findViewById(R.id.title);
            cardCategory = (TextView) itemView.findViewById(R.id.card_category);
            cardSaveButton = (ImageView) itemView.findViewById(R.id.save);
            newsBase = (LinearLayout) itemView.findViewById(R.id.news_base);
            likesCount = (TextView) itemView.findViewById(R.id.like_count);
        }
    }

    public static class LargeCardHolder extends RecyclerView.ViewHolder {

        public ImageView cardImage;
        public TextView cardTitle;
        public TextView cardCategory;
        public ImageView cardSaveButton;
        public CardView newsCard;
        private TextView likesCount;

        public LargeCardHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            cardTitle = (TextView) itemView.findViewById(R.id.title);
            cardCategory = (TextView) itemView.findViewById(R.id.card_category);
            cardSaveButton = (ImageView) itemView.findViewById(R.id.save);
            newsCard = (CardView) itemView.findViewById(R.id.news_card);
            likesCount = (TextView) itemView.findViewById(R.id.like_count);
        }
    }

    public static class CondensedCardHolder extends RecyclerView.ViewHolder {

        public TextView cardTitle;
        public TextView cardCategory;
        public ImageView cardSaveButton;
        public CardView newsCard;
        private TextView likesCount;

        public CondensedCardHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.title);
            cardCategory = (TextView) itemView.findViewById(R.id.card_category);
            cardSaveButton = (ImageView) itemView.findViewById(R.id.save);
            newsCard = (CardView) itemView.findViewById(R.id.news_card);
            likesCount = (TextView) itemView.findViewById(R.id.like_count);
        }
    }

    private static class AdHolder extends RecyclerView.ViewHolder {

        public BadgedFourThreeImageView adImage;

        public AdHolder(View itemView) {
            super(itemView);
            adImage = (BadgedFourThreeImageView) itemView.findViewById(R.id.ad_image);
        }
    }

    private static class StubHolder extends RecyclerView.ViewHolder {

        public TextView endText;

        public StubHolder(View itemView) {
            super(itemView);
            endText = (TextView) itemView.findViewById(R.id.end_text);
        }
    }

    private AdHolder createAdHolder(ViewGroup parent) {
        //todo : check for crash and change badgedimageview
        final AdHolder holder = new AdHolder(layoutInflater
                .inflate(R.layout.item_advertisement, parent, false));
        holder.adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http:"+
                        ((Advert) mLoadList.get(holder.getAdapterPosition())).url
                )));
            }
        });
        return holder;
    }

    private CardHolder createCardHolder(ViewGroup parent) {
        final CardHolder holder = new CardHolder(layoutInflater
        .inflate(R.layout.item_info_card, parent, false));
        return holder;
    }

    private LargeCardHolder createLargeCardHolder(ViewGroup parent) {
        final LargeCardHolder holder = new LargeCardHolder(layoutInflater
        .inflate(R.layout.itemview_article_important, parent, false));
        return holder;
    }

    private CondensedCardHolder createCondensedCardHolder(ViewGroup parent) {
        final CondensedCardHolder holder = new CondensedCardHolder(layoutInflater
        .inflate(R.layout.itemview_article_condensed, parent, false));
        return holder;
    }

    private StubHolder createStubHolder(ViewGroup parent) {
        return new StubHolder(layoutInflater
        .inflate(R.layout.itemview_stub, parent, false));
    }

    private void bindCardView(final Story story, final CardHolder holder){
        Log.d(TAG, String.valueOf(story.id));
        holder.newsBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleViewIntent = new Intent(mContext, ArticleActivity.class);
                articleViewIntent.putExtra(Story.INTENT_EXTRA, story);
                Log.d(TAG, story.title);
                mContext.startActivity(articleViewIntent);
            }
        });
        holder.cardTitle.setText(story.title.trim());
        holder.likesCount.setText(String.valueOf(story.likes));
        int saveDrawableResource = story.isSaved ?
                        R.drawable.ic_saved_offline : R.drawable.ic_save_offline;
        holder.cardSaveButton.setImageResource(saveDrawableResource);
        Glide.with(mContext)
                .load(story.link_image.get(0))
                .crossFade()
                .centerCrop()
                .placeholder(R.color.dark_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cardImage);
    }

    private void bindLargeCardView(Story story, LargeCardHolder holder) {
        holder.cardTitle.setText(story.title.trim());
        holder.likesCount.setText(String.valueOf(story.likes));
        holder.cardSaveButton.setImageResource(R.drawable.ic_saved_offline);
        Glide.with(mContext)
                .load(story.link_image.get(0))
                .crossFade()
                .centerCrop()
                .listener(getFadeInRequestListener(holder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cardImage);
    }

    private void bindCondensedCardView(Story story, CondensedCardHolder holder) {
        holder.cardTitle.setText(story.title.trim());
    }

    private void bindAdvertisementView(Advert ad, AdHolder holder) {
        Glide.with(mContext)
                .load(ad.link_image)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.adImage);
    }

    private void bindStubView(StubHolder holder) {
        holder.endText.setText(R.string.copyright);
    }

    private RequestListener<String, GlideDrawable>
    getFadeInRequestListener(final LargeCardHolder holder) {
        return new RequestListener<String, GlideDrawable>() {

            @Override
            public boolean onResourceReady(GlideDrawable resource,
                                           String model,
                                           Target<GlideDrawable> target,
                                           boolean isFromMemoryCache,
                                           boolean isFirstResource) {
                if (!((Story)mLoadList.get(holder.getAdapterPosition())).hasAnimated) {
                    holder.cardImage.setHasTransientState(true);
                    final ObservableColorMatrix cm = new ObservableColorMatrix();
                    final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                            cm, ObservableColorMatrix.SATURATION, 0f, 1f);
                    saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                            () {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            // just animating the color matrix does not invalidate the
                            // drawable so need this update listener.  Also have to create a
                            // new CMCF as the matrix is immutable :(
                            holder.cardImage.setColorFilter(new ColorMatrixColorFilter(cm));
                        }
                    });
                    saturation.setDuration(2000L);
                    saturation.setInterpolator(new FastOutSlowInInterpolator());
                    saturation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.cardImage.clearColorFilter();
                            holder.cardImage.setHasTransientState(false);
                        }
                    });
                    saturation.start();
                    ((Story)mLoadList.get(holder.getAdapterPosition())).hasAnimated = true;
                }
                return false;
            }

            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable>
                    target, boolean isFirstResource) {
                return false;
            }
        };
    }

}
