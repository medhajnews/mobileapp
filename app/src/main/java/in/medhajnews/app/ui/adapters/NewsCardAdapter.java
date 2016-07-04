package in.medhajnews.app.ui.adapters;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import in.medhajnews.app.R;
import in.medhajnews.app.data.api.models.Advert;
import in.medhajnews.app.data.api.models.Photo;
import in.medhajnews.app.data.api.models.Story;
import in.medhajnews.app.data.objects.DataItem;
import in.medhajnews.app.ui.ArticleActivity;
import in.medhajnews.app.ui.NewsActivity;
import in.medhajnews.app.ui.widget.BadgedFourThreeImageView;
import in.medhajnews.app.util.ObservableColorMatrix;

/**
 * Created by bhav on 6/9/16 for the Medhaj News Project.
 */
public class NewsCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = NewsCardAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_LARGE = 1;
    private static final int VIEW_TYPE_NORMAL = 2; // ? need design
    private static final int VIEW_TYPE_CONDENSED = 3;
    private static final int VIEW_TYPE_STUB = 4;
    private static final int VIEW_TYPE_PHOTO = 5;
    private static final int VIEW_TYPE_TWIN = 6;
    // use VIEW_TYPE advertisement to insert ads into the recyclerview
    private static final int VIEW_TYPE_ADVERTISEMENT = 9;


    private Context mContext;
    public List<DataItem> mLoadList;
    private boolean showCategory;
    private LayoutInflater layoutInflater;

    public NewsCardAdapter(Context context, List<DataItem> items, boolean isRecommendedSection) {
        this.mContext = context;
        this.mLoadList = items;
        this.showCategory = isRecommendedSection;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ADVERTISEMENT:
                return createAdHolder(parent);
            case VIEW_TYPE_LARGE:
                return createLargeCardHolder(parent);
            case VIEW_TYPE_NORMAL:
                return createCardHolder(parent);
            case VIEW_TYPE_CONDENSED:
                return createCondensedCardHolder(parent);
            case VIEW_TYPE_STUB:
                return createStubHolder(parent);
            case VIEW_TYPE_PHOTO:
                return createPhotoViewHolder(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ADVERTISEMENT:
                bindAdvertisementView((Advert) mLoadList.get(position), (AdHolder) holder);
                break;
            case VIEW_TYPE_CONDENSED:
                bindCondensedCardView((Story) mLoadList.get(position), (CondensedCardHolder) holder);
                break;
            case VIEW_TYPE_LARGE:
                bindLargeCardView((Story) mLoadList.get(position), (LargeCardHolder) holder);
                break;
            case VIEW_TYPE_NORMAL:
                bindCardView((Story) mLoadList.get(position), (CardHolder) holder);
                break;
            case VIEW_TYPE_STUB:
                bindStubView((StubHolder) holder);
                break;
            case VIEW_TYPE_PHOTO:
                bindPhotoView((Photo) mLoadList.get(position), (PhotoHolder) holder);
        }
    }

    private int getDataItemCount() {
        return mLoadList.size();
    }

    private DataItem getItemAt(int position) {
        return mLoadList.get(position);
    }

    private boolean isStoryShort(int position) {
        return (mLoadList.get(position)).title.length() <= 32;
    }

    @Override
    public int getItemCount() {
        return mLoadList.size() == 0 ? 0 : mLoadList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount() && getDataItemCount() > 0) {
            if (getItemAt(position) instanceof Advert) {
                return VIEW_TYPE_ADVERTISEMENT;
            } else if (getItemAt(position) instanceof Photo) {
                return VIEW_TYPE_PHOTO;
            } else if (getItemAt(position) instanceof Story) {
                if (((Story) getItemAt(position)).type.toLowerCase().equals("large")) {
                    return VIEW_TYPE_LARGE;
                } else if (isStoryShort(position) ||
                        ((Story) getItemAt(position)).type.toLowerCase().equals("tiny")) {
                    return VIEW_TYPE_CONDENSED;
                } else {
                    return VIEW_TYPE_NORMAL;
                }
            }
        }
        return VIEW_TYPE_STUB;
    }


    /* View Holders */

     static class CardHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView category;
        ImageView saveButton;
        LinearLayout base;
        TextView likesCount;

        public CardHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.card_image);
            title = (TextView) itemView.findViewById(R.id.title);
            category = (TextView) itemView.findViewById(R.id.card_category);
            saveButton = (ImageView) itemView.findViewById(R.id.save);
            base = (LinearLayout) itemView.findViewById(R.id.news_base);
            likesCount = (TextView) itemView.findViewById(R.id.like_count);
        }
    }

    static class LargeCardHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView category;
        ImageView saveButton;
        CardView newsCard;
        TextView likesCount;

        public LargeCardHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.card_image);
            title = (TextView) itemView.findViewById(R.id.title);
            category = (TextView) itemView.findViewById(R.id.card_category);
            saveButton = (ImageView) itemView.findViewById(R.id.save);
            newsCard = (CardView) itemView.findViewById(R.id.news_card);
            likesCount = (TextView) itemView.findViewById(R.id.like_count);
        }
    }

    static class CondensedCardHolder extends RecyclerView.ViewHolder {

        TextView cardTitle;
        TextView cardCategory;
        ImageView cardSaveButton;
         CardView newsCard;
         TextView likesCount;

        public CondensedCardHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.title);
            cardCategory = (TextView) itemView.findViewById(R.id.card_category);
            cardSaveButton = (ImageView) itemView.findViewById(R.id.save);
            newsCard = (CardView) itemView.findViewById(R.id.news_card);
            likesCount = (TextView) itemView.findViewById(R.id.like_count);
        }
    }

    static class PhotoHolder extends RecyclerView.ViewHolder {
         BadgedFourThreeImageView photoStory;

        public PhotoHolder(View itemView) {
            super(itemView);
            photoStory = (BadgedFourThreeImageView) itemView.findViewById(R.id.main_image);
        }
    }

     static class AdHolder extends RecyclerView.ViewHolder {

         BadgedFourThreeImageView adImage;
         TextView adTitle;
         TextView adDesc;

        public AdHolder(View itemView) {
            super(itemView);
            adImage = (BadgedFourThreeImageView) itemView.findViewById(R.id.ad_image);
            adTitle = (TextView) itemView.findViewById(R.id.ad_title);
            adDesc = (TextView) itemView.findViewById(R.id.ad_desc);
        }
    }

    static class StubHolder extends RecyclerView.ViewHolder {

         TextView endText;

        public StubHolder(View itemView) {
            super(itemView);
            endText = (TextView) itemView.findViewById(R.id.end_text);
        }
    }

    /* View Creators */
    private AdHolder createAdHolder(ViewGroup parent) {
        final AdHolder holder = new AdHolder(layoutInflater
                .inflate(R.layout.itemview_adverts, parent, false));
        holder.adImage.showBadge(true);
        holder.adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http:" +
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
                .inflate(R.layout.itemview_story_large, parent, false));
        return holder;
    }

    private CondensedCardHolder createCondensedCardHolder(ViewGroup parent) {
        final CondensedCardHolder holder = new CondensedCardHolder(layoutInflater
                .inflate(R.layout.itemview_story, parent, false));
        return holder;
    }

    private StubHolder createStubHolder(ViewGroup parent) {
        return new StubHolder(layoutInflater
                .inflate(R.layout.itemview_stub, parent, false));
    }

    private PhotoHolder createPhotoViewHolder(ViewGroup parent) {
        final PhotoHolder holder = new PhotoHolder(layoutInflater
                .inflate(R.layout.itemview_image_story, parent, false));
        holder.photoStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Photo Article", Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    /* View Binders */
    private void bindPhotoView(final Photo photo, final PhotoHolder holder) {
        Glide.with(mContext)
                .load(photo.link_image.get(0))
                .crossFade()
//                .centerCrop()
                .placeholder(R.color.dark_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.photoStory);
    }

    private void bindCardView(final Story story, final CardHolder holder) {
        holder.base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleViewIntent = new Intent(mContext, ArticleActivity.class);
                articleViewIntent.putExtra(Story.INTENT_EXTRA, story);
                Log.d(TAG, story.title);
                mContext.startActivity(articleViewIntent);
            }
        });
        holder.title.setText(story.title.trim());
        holder.likesCount.setText(String.valueOf(story.likes));
        int saveDrawableResource = story.isSaved ?
                R.drawable.ic_saved_offline : R.drawable.ic_save_offline;
        holder.saveButton.setImageResource(saveDrawableResource);
        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(story.isSaved) {
                    ((NewsActivity)mContext).articleDB.DeleteArticle(story);
                } else {
                    ((NewsActivity)mContext).articleDB.SaveArticle(story);
                }
                int saveDrawableResource = story.isSaved ?
                        R.drawable.ic_saved_offline : R.drawable.ic_save_offline;
                holder.saveButton.setImageResource(saveDrawableResource);
            }
        });
        Glide.with(mContext)
                .load(story.link_image.get(0))
                .crossFade()
//                .centerCrop()  //backend NEEDS to crop images,
                                // implementing it in the app alone will look fugly
                .placeholder(R.color.dark_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
    }

    private void bindLargeCardView(final Story story, final LargeCardHolder holder) {
        holder.title.setText(story.title.trim());
        holder.likesCount.setText(String.valueOf(story.likes));
        holder.saveButton.setImageResource(R.drawable.ic_saved_offline);
        holder.newsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ArticleActivity.class)
                        .putExtra(Story.INTENT_EXTRA, story));
            }
        });
        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(story.isSaved) {
                    ((NewsActivity)mContext).articleDB.DeleteArticle(story);
                } else {
                    ((NewsActivity)mContext).articleDB.SaveArticle(story);
                }
                int saveDrawableResource = story.isSaved ?
                        R.drawable.ic_saved_offline : R.drawable.ic_save_offline;
                holder.saveButton.setImageResource(saveDrawableResource);
            }
        });
        Glide.with(mContext)
                .load(story.link_image.get(0))
                .crossFade()
//                .centerCrop()
                .listener(getFadeInRequestListener(holder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
    }

    private void bindCondensedCardView(final Story story, CondensedCardHolder holder) {
        holder.cardTitle.setText(story.title.trim());
        holder.newsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ArticleActivity.class)
                        .putExtra(Story.INTENT_EXTRA, story));
            }
        });
        int saveDrawableResource = story.isSaved ?
                R.drawable.ic_saved_offline : R.drawable.ic_save_offline;
        holder.cardSaveButton.setImageResource(saveDrawableResource);
    }

    private void bindAdvertisementView(Advert ad, AdHolder holder) {
        holder.adTitle.setText(ad.title);
        holder.adDesc.setText(ad.content);
        Glide.with(mContext)
                .load(ad.link_image)
                .crossFade()
//                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.adImage);
    }

    private void bindStubView(StubHolder holder) {
        holder.endText.setText(R.string.copyright);
    }


    /*  Glide Load Callbacks */
    private RequestListener<String, GlideDrawable>
    getFadeInRequestListener(final LargeCardHolder holder) {
        return new RequestListener<String, GlideDrawable>() {

            @Override
            public boolean onResourceReady(GlideDrawable resource,
                                           String model,
                                           Target<GlideDrawable> target,
                                           boolean isFromMemoryCache,
                                           boolean isFirstResource) {
                if (!((Story) mLoadList.get(holder.getAdapterPosition())).hasAnimated) {
                    holder.image.setHasTransientState(true);
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
                            holder.image.setColorFilter(new ColorMatrixColorFilter(cm));
                        }
                    });
                    saturation.setDuration(2000L);
                    saturation.setInterpolator(new FastOutSlowInInterpolator());
                    saturation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.image.clearColorFilter();
                            holder.image.setHasTransientState(false);
                        }
                    });
                    saturation.start();
                    ((Story) mLoadList.get(holder.getAdapterPosition())).hasAnimated = true;
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
