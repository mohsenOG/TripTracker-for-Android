package eu.wonderfulme.triptracker.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import eu.wonderfulme.triptracker.database.LocationHeaderData;
import eu.wonderfulme.triptracker.database.LocationRepository;

public class MainActivityViewModel extends AndroidViewModel {

    private final LocationRepository mLocationRepository;
    private final LiveData<List<LocationHeaderData>> mAllHeaders;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mLocationRepository = new LocationRepository(application);
        mAllHeaders = mLocationRepository.getAllLocationHeaderData();
    }

    public LiveData<List<LocationHeaderData>> getAllHeaders() {
        return mAllHeaders;
    }
}
