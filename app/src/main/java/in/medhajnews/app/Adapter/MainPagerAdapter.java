package in.medhajnews.app.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import in.medhajnews.app.Fragments.MainFragment;
import in.medhajnews.app.Objects.Article;

/**
 * Created by bhav on 6/10/16 for the Medhaj News Project.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Article> mArticleList;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
//        this.mArticleList = articleList;
    }

    @Override
    public Fragment getItem(int position) {
        return MainFragment.newInstance(position == 0);
    }

    @Override
    public int getCount() {
        // Show 5 total pages.
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "FOR YOu";
            case 1:
                return "INDIA";
            case 2:
                return "WORLD";
            case 3:
                return "SPORTS";
            case 4:
                return "SCIENCE";
        }
        return null;
    }
}