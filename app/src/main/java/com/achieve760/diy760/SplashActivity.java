package com.achieve760.diy760;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Make the splash screen full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        // Find the ImageView containing your logo
        ImageView logo = findViewById(R.id.logo); // Use the updated ID from the layout file

        // Load the fade-in-out animation
        Animation fadeInOut = AnimationUtils.loadAnimation(this, R.anim.fade_in_out);

        // Set an AnimationListener to navigate to the login screen when the animation ends
        fadeInOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                logo.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(SplashActivity.this, Splash2Activity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Add this line to remove the visible transition
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

        });

        // Apply the animation to the logo
        logo.startAnimation(fadeInOut);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0); // Add this line to remove the visible transition
    }

}