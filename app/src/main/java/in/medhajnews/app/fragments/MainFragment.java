package in.medhajnews.app.fragments;

/**
 * Created by bhav on 6/9/16 for the Medhaj News Project.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.medhajnews.app.R;
import in.medhajnews.app.adapters.NewsCardAdapter;
import in.medhajnews.app.animators.LandingAnimator;
import in.medhajnews.app.data.objects.DataItem;
import in.medhajnews.app.data.models.Story;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION = "section_type";
    public static RecyclerView mainRecyclerView;
    public NewsCardAdapter newsCardAdapter;
    public static List<DataItem> ArticleList;
    LinearLayoutManager mLinearLayoutManager;
    private boolean mLoading = false;
    private boolean mIsRecommendedPage = false;


    public MainFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(List<DataItem> itemList, boolean isRecommendedSection) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SECTION, isRecommendedSection);
        fragment.setArguments(args);
        MainFragment.ArticleList = itemList;
        return fragment;
    }

    @Override
    public void onResume() {
        if(newsCardAdapter!=null) {
            newsCardAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        if(newsCardAdapter!=null) {
            newsCardAdapter.notifyDataSetChanged();
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        mainRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recyclerview);
        mIsRecommendedPage = getArguments().getBoolean(ARG_SECTION);

        mainRecyclerView.setItemAnimator(new LandingAnimator());
        newsCardAdapter = new NewsCardAdapter(getContext(), ArticleList,
                mIsRecommendedPage);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        StaggeredGridLayoutManager mStaggeredLayoutManager =
                new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        mStaggeredLayoutManager.setGapStrategy(
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        // load everything into a staggered grid layout for tablets
        if (getResources().getBoolean(R.bool.isTab)) {
            mainRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        } else {
            mainRecyclerView.setLayoutManager(mLinearLayoutManager);
        }
        mainRecyclerView.setItemAnimator(new LandingAnimator(new FastOutSlowInInterpolator()));
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setAdapter(newsCardAdapter);
//        mainRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
//            @Override
//            public void onLoadMore(int current_page) {
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("https://tranquil-dusk-46393.herokuapp.com")
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//
//                DataFetcher fetcher = retrofit.create(DataFetcher.class);
//                Call<ArrayList<Story>> call = fetcher.getArticles();
//                final ArrayList<Story> articles = new ArrayList<>();
//                call.enqueue(new Callback<ArrayList<Story>>() {
//                    @Override
//                    public void onResponse(Call<ArrayList<Story>> call, Response<ArrayList<Story>> response) {
//                        if(response.isSuccessful()) {
//                            for(Story a : response.body()) {
//                                a.ArticleTitle = a.ArticleTitle.trim();
//                                articles.add(a);
//                            }
//                            int initial_count = newsCardAdapter.getItemCount();
//                            newsCardAdapter.mArticleList.addAll(initial_count-1, articles);
////                            newsCardAdapter.notifyItemRangeInserted(initial_count-1, articles.size());
//                            newsCardAdapter.notifyDataSetChanged();
//                        }
//                        else {
//                            Log.d(TAG, "Request failed with error code: " + response.code());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ArrayList<Story>> call, Throwable t) {
//                        Log.d(TAG, "failed to load");
//                        //todo : handle error
//                    }
//                });
//                Log.d(TAG, "onLoadMore: ");
//            }
//        });
        return rootView;
    }

    public void addItem(Story a, int position){
        if(newsCardAdapter.mLoadList!=null) {
            newsCardAdapter.mLoadList.add(position, a);
            newsCardAdapter.notifyItemInserted(position);
        }
    }
}