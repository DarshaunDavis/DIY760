package com.achieve760.diy760;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> generatedLetter = new MutableLiveData<>();

    public void setGeneratedLetter(String letter) {
        generatedLetter.setValue(letter);
    }

    public LiveData<String> getGeneratedLetter() {
        return generatedLetter;
    }
}
