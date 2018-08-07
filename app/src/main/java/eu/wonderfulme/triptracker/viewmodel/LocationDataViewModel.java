package eu.wonderfulme.triptracker.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import eu.wonderfulme.triptracker.database.LocationHeaderData;
import eu.wonderfulme.triptracker.database.LocationRepository;

public class LocationDataViewModel extends AndroidViewModel {

    private LocationRepository mLocationRepository;
    private LiveData<List<LocationHeaderData>> mAllHeaders;

    public LocationDataViewModel(@NonNull Application application) {
        super(application);
        mLocationRepository = new LocationRepository(application);
        mAllHeaders = mLocationRepository.getAllLocationHeaderData();
    }

    public LiveData<List<LocationHeaderData>> getAllHeaders() {
        return mAllHeaders;
    }
}
