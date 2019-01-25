package eu.wonderfulme.triptracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import eu.wonderfulme.triptracker.database.LocationHeaderData;
import eu.wonderfulme.triptracker.database.LocationRepository;

public class LocationDataViewModel extends AndroidViewModel {

    private final LocationRepository mLocationRepository;
    private final LiveData<List<LocationHeaderData>> mAllHeaders;

    public LocationDataViewModel(@NonNull Application application) {
        super(application);
        mLocationRepository = new LocationRepository(application);
        mAllHeaders = mLocationRepository.getAllLocationHeaderData();
    }

    public LiveData<List<LocationHeaderData>> getAllHeaders() {
        return mAllHeaders;
    }
}
