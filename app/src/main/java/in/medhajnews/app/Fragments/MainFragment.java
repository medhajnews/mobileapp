package in.medhajnews.app.Fragments;

/**
 * Created by bhav on 6/9/16 for the Medhaj News Project.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.medhajnews.app.Adapter.NewsCardAdapter;
import in.medhajnews.app.Objects.Article;
import in.medhajnews.app.R;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_LIST = "articles";


    public MainFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(boolean twoViewtypes) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_LIST, twoViewtypes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        RecyclerView mainRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recyclerview);
        ArrayList<Article> DummyArticleList = new ArrayList<Article>();

        for(int i = 0; i< 25; i++) {
            DummyArticleList.add(Article.sampleArticle(getContext()));
        }

        mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
        NewsCardAdapter newsCardAdapter = new NewsCardAdapter(getContext(), DummyArticleList,
                getArguments().getBoolean(ARG_LIST));
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setAdapter(newsCardAdapter);
        return rootView;
    }
}