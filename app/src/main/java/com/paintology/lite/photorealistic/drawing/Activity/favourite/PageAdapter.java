package com.paintology.lite.photorealistic.drawing.Activity.favourite;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.paintology.lite.photorealistic.drawing.Fragment.MainCollectionFragment;

public class PageAdapter
        extends FragmentPagerAdapter {

    public PageAdapter(
            @NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new GalleryFav("user");
        else if (position == 1)
            fragment = new GalleryFav("gallery");
        else if (position == 2)
            fragment = new GalleryFav("tutorial");

        else if (position == 3)
            fragment = new MainCollectionFragment(true);

        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;

        if (position == 0)

            title = "Users";

        else if (position == 1)
            title = "Gallery";

        else if (position == 2)

            title = "Tutorials";

        else if (position == 3)

            title = "Community";


        return title;
    }
}
