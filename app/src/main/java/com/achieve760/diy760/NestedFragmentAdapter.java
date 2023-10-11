package com.achieve760.diy760;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NestedFragmentAdapter extends FragmentStateAdapter {

    public NestedFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GenerateFragment();
            case 1:
                return new ViewFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
