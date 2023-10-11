package com.achieve760.diy760;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements OnGenerateLetterClickListener, GenerateFragment.UnsavedChangesCallback {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private boolean hasUnsavedChanges;
    private AdView mAdView;
    private ViewPager2 viewPager;
    private CustomFragmentStateAdapter adapter;
    private TabLayout tabLayout;

    @Override
    public void onUnsavedChanges(boolean hasUnsavedChanges) {
        this.hasUnsavedChanges = hasUnsavedChanges;
    }

    public boolean hasUnsavedChanges() {
        return this.hasUnsavedChanges;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new CustomFragmentStateAdapter(this);

        adapter.addFragment(new LeftFragment());
        adapter.addFragment(new MainFragment());
        adapter.addFragment(new RightFragment());

        viewPager.setAdapter(adapter);

        // Set the starting position to the MainFragment
        viewPager.setCurrentItem(1, false);

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Find the AdView and load an ad
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Set up the TabLayoutMediator to connect ViewPager2 with the TabLayout
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // You can set tab text or use custom views for tabs
            switch (position) {
                case 0:
                    tab.setText("Learn");
                    break;
                case 1:
                    tab.setText("Main");
                    break;
                case 2:
                    tab.setText("Repair");
                    break;
            }
        }).attach();
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tap back button twice to exit",
                    Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onGenerateLetterClick(String generatedLetter) {
        GeneratedLetter generatedLetterFragment = GeneratedLetter.newInstance(generatedLetter);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.viewPager, generatedLetterFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Handle settings action here
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}