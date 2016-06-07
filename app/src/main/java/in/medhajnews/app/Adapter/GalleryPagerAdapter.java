package in.medhajnews.app.Adapter;

/**
 * Created by bhav on 6/7/16 for the Medhaj News Project.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.medhajnews.app.Fragments.ImageFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class GalleryPagerAdapter extends FragmentPagerAdapter {

    private int NUM_OF_PAGES = 3;

    public GalleryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return ImageFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return NUM_OF_PAGES;
    }

    public void setCount(int pages) {
        this.NUM_OF_PAGES = pages;
    }

//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            case 0:
//                return "SECTION 1";
//            case 1:
//                return "SECTION 2";
//            case 2:
//                return "SECTION 3";
//        }
//        return null;
//    }
}
