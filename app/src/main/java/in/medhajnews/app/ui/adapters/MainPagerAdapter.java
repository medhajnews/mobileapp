package in.medhajnews.app.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import in.medhajnews.app.ui.fragments.MainFragment;
import in.medhajnews.app.data.objects.DataItem;

/**
 * Created by bhav on 6/10/16 for the Medhaj News Project.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private List<DataItem> mArticleList;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private static final int PAGE_COUNT = 5;

    public MainPagerAdapter(FragmentManager fm, List<DataItem> articleArrayList) {
        super(fm);
        this.mArticleList = articleArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return MainFragment.newInstance(mArticleList, position==0);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    /**
     * Don't forget to add Category priorities in {@link in.medhajnews.app.data.Priority}
     * on adding more tabs.
     */
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

    /**
     * Method to access fragment loaded by the viewpager
     * @param position (int) position to get
     * @return (Fragment) loaded in the viewpager. Maybe null if fragment hasn't been instantiated
     */
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}