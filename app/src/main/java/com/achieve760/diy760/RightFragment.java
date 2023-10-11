package com.achieve760.diy760;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.achieve760.diy760.NestedFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RightFragment extends Fragment {

    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new NestedFragmentAdapter(this));

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Generate");
                    break;
                case 1:
                    tab.setText("View");
                    break;
            }
        }).attach();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // ... other code

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position != 0 && ((MainActivity) getActivity()).hasUnsavedChanges()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Unsaved changes")
                            .setMessage("Any unsaved changes will be lost.")
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Clear all of the existing resources in the GenerateFragment
                                    // You'll have to set up communication from the RightFragment to the GenerateFragment to do this
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // User cancelled the dialog
                                    // Navigate back to the GenerateFragment
                                    viewPager.setCurrentItem(0, true);
                                }
                            })
                            .show();
                }
            }

        });

    }
}