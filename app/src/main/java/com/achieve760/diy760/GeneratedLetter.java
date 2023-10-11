package com.achieve760.diy760;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.TextView;

public class GeneratedLetter extends Fragment {

    private TextView generatedLetterTextView;

    // Declare your variables here

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generatedLetterTextView = view.findViewById(R.id.generated_letter_textview);

        Bundle args = getArguments();
        if (args != null) {
            String generatedLetter = args.getString("generatedLetter");
            if (generatedLetter != null) {
                generatedLetterTextView.setText(generatedLetter);
            }
        }
    }

    public static GeneratedLetter newInstance(String generatedLetter) {
        GeneratedLetter fragment = new GeneratedLetter();
        Bundle args = new Bundle();
        args.putString("generatedLetter", generatedLetter);
        fragment.setArguments(args);
        return fragment;
    }

}