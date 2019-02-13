package eu.wonderfulme.triptracker.viewmodel;

import android.app.Application;
import android.content.Context;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import eu.wonderfulme.triptracker.database.LocationData;
import eu.wonderfulme.triptracker.database.LocationRepository;

public class DetailActivityViewModel extends AndroidViewModel {

    private LocationRepository mLocationRepository;
    private int mItemKey;
    private LiveData<List<LocationData>> mLocationData;


    public DetailActivityViewModel(@NonNull Application application, int itemKey) {
        super(application);
        mLocationRepository = new LocationRepository(application);
        this.mItemKey = itemKey;
        mLocationData = mLocationRepository.getLocationDataPerItemKey(this.mItemKey);
    }

    public int getItemKey() {return mItemKey;}

    public LiveData<List<LocationData>> getLocationData() {
        return mLocationData;
    }

    public void deleteSingleItemKey(Context context, Snackbar snackbar) {
        mLocationRepository.deleteSingleItemKey(context, snackbar, mItemKey);
    }
}
