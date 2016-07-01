package in.medhajnews.app.fragments;

/**
 * Created by bhav on 6/7/16 for the Medhaj News Project.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import in.medhajnews.app.R;

/**
 * A Image fragment containing a simple view.
 */
public class ImageFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ImageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ImageFragment newInstance(int sectionNumber) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        return rootView;
    }
}