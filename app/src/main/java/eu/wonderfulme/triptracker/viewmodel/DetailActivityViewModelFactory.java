package eu.wonderfulme.triptracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailActivityViewModelFactory implements ViewModelProvider.Factory {

    private int mItemKey;
    private Application mApplication;

    public DetailActivityViewModelFactory(@NonNull Application application, int itemKey) {
        mApplication = application;
        mItemKey = itemKey;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailActivityViewModel(mApplication, mItemKey);
    }
}